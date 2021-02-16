package com.wutsi.blog.app.page.blog

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.PinModel
import com.wutsi.blog.app.page.blog.service.PinService
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.schemas.PersonSchemasGenerator
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType
import com.wutsi.blog.client.story.SortAlgorithmType.most_recent
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class BlogController(
    private val userService: UserService,
    private val followerService: FollowerService,
    private val storyService: StoryService,
    private val schemas: PersonSchemasGenerator,
    private val pinService: PinService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    companion object {
        const val STORY_PAGE_SIZE: Int = 10
        const val VIEWED_STORIES_TTL_HOURS: Int = 7 * 24
    }

    override fun pageName() = PageName.BLOG

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    @GetMapping("/@/{name}")
    fun index(@PathVariable name: String, model: Model): String {
        val blog = userService.get(name)

        model.addAttribute("blog", blog)
        model.addAttribute("page", getPage(blog))

        val followingUserIds = followerService.searchFollowingUserIds()
            .filter { it != blog.id }

        return if (blog.blog)
            loadWriter(followingUserIds, blog, model)
        else
            loadReader(followingUserIds, blog, model)
    }

    @GetMapping("/@/{name}/my-stories")
    fun myStories(@PathVariable name: String, @RequestParam offset: Int, model: Model): String {
        val blog = userService.get(name)
        val stories = loadMyStories(blog, null, model, offset)

        model.addAttribute("blog", blog)
        model.addAttribute("stories", stories)

        return "page/blog/stories"
    }

    private fun loadWriter(followingUserIds: List<Long>, blog: UserModel, model: Model): String {
        val pin = loadPin(blog, model)
        val stories = loadMyStories(blog, pin, model)

        loadFollowingStories(followingUserIds, model, 10)
        loadLatestStories(blog, followingUserIds, model)
        shouldShowFollowButton(blog, model)
        shouldShowCreateStory(blog, stories, model)
        return "page/blog/writer"
    }

    private fun loadMyStories(blog: UserModel, pin: PinModel?, model: Model, offset: Int = 0): List<StoryModel> {
        val limit = STORY_PAGE_SIZE
        val stories = storyService.search(
            pin = pin,
            request = SearchStoryRequest(
                userIds = listOf(blog.id),
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.published,
                sortOrder = SortOrder.descending,
                limit = limit,
                offset = offset
            )
        )

        val xstories = bubbleDownViewedStories(stories, blog)
        val result = pinStory(xstories, pin?.storyId)

        model.addAttribute("myStories", result)

        if (result.size >= limit) {
            val nextOffset = offset + limit;
            model.addAttribute("moreUrl", "/@/${blog.name}/my-stories?offset=$nextOffset")
            model.addAttribute("nextOffset", nextOffset)
            model.addAttribute("offset", offset)
        }
        return result
    }

    private fun loadFollowingStories(followingUserIds: List<Long>, model: Model, limit: Int) {
        // Find following users
        if (followingUserIds.isEmpty())
            return

        // Find stories from following users
        val stories = storyService.search(
            SearchStoryRequest(
                userIds = followingUserIds,
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.published,
                sortOrder = SortOrder.descending,
                limit = limit
            )
        )
        val followingStories = storyService.sort(
            stories = stories,
            algorithm = SortAlgorithmType.no_sort,
            bubbleDownViewedStories = true,
            statsHoursOffset = 7 * 24
        )
        model.addAttribute("followingStories", followingStories)
    }

    private fun loadLatestStories(blog: UserModel, followingUserIds: List<Long>, model: Model) {
        val latestStoryMap = LinkedHashMap<UserModel, StoryModel>()
        storyService.search(
            SearchStoryRequest(
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.published,
                sortOrder = SortOrder.descending,
                limit = 50
            )
        )
            .filter { it.user.id != blog.id && !followingUserIds.contains(it.user.id) }
            .forEach {
                val user = it.user
                if (!latestStoryMap.containsKey(user)) {
                    latestStoryMap[user] = it
                }
            }
        val latestStories = storyService.sort(
            stories = latestStoryMap.values.toList(),
            algorithm = SortAlgorithmType.no_sort,
            bubbleDownViewedStories = true,
            statsHoursOffset = 7 * 24
        )

        model.addAttribute("latestStories", latestStories.take(5))
    }

    private fun bubbleDownViewedStories(stories: List<StoryModel>, blog: UserModel): List<StoryModel> {
        if (!shouldBubbleDownViewedStories(stories, blog))
            return stories

        return storyService.sort(
            stories = stories,
            bubbleDownViewedStories = true,
            algorithm = most_recent,
            statsHoursOffset = VIEWED_STORIES_TTL_HOURS
        )
    }

    private fun shouldBubbleDownViewedStories(stories: List<StoryModel>, blog: UserModel): Boolean =
        stories.isNotEmpty() && blog.id != requestContext.currentUser()?.id

    private fun pinStory(stories: List<StoryModel>, pinnedStoryId: Long?): List<StoryModel> {
        val pinnedStory = stories.find { it.id == pinnedStoryId }
            ?: return stories

        val result = mutableListOf<StoryModel>()
        result.add(pinnedStory)
        result.addAll(stories.filter { it.id != pinnedStory.id })
        return result
    }

    private fun loadReader(followingUserIds: List<Long>, blog: UserModel, model: Model): String {
        loadFollowingStories(followingUserIds, model, 50)
        loadLatestStories(blog, followingUserIds, model)
        return "page/blog/reader"
    }

    private fun loadPin(blog: UserModel, model: Model): PinModel? {
        if (!requestContext.toggles().pin)
            return null

        val pin = pinService.get(blog)
        model.addAttribute("pin", pin)
        return pin
    }

    private fun shouldShowFollowButton(blog: UserModel, model: Model) {
        model.addAttribute("showFollowButton", followerService.canFollow(blog.id))
    }

    private fun shouldShowCreateStory(blog: UserModel, stories: List<StoryModel>, model: Model) {
        if (stories.isNotEmpty() || !blog.blog || blog.id != requestContext.currentUser()?.id)
            return

        val count = storyService.count()
        model.addAttribute("showCreateStoryButton", count == 0)
    }

    protected fun getPage(user: UserModel) = createPage(
        name = pageName(),
        title = user.fullName,
        description = if (user.biography == null) "" else user.biography,
        type = "profile",
        url = url(user),
        imageUrl = user.pictureUrl,
        schemas = schemas.generate(user),
        rssUrl = "${user.slug}/rss"
    )
}
