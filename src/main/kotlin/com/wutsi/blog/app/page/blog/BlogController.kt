package com.wutsi.blog.app.page.blog

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.NextActionModel
import com.wutsi.blog.app.page.blog.model.PinModel
import com.wutsi.blog.app.page.blog.service.PinService
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.schemas.PersonSchemasGenerator
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BlogController(
        private val userService: UserService,
        private val followerService: FollowerService,
        private val storyService: StoryService,
        private val schemas: PersonSchemasGenerator,
        private val channelService: ChannelService,
        private val pinService: PinService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
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

    private fun loadWriter(followingUserIds: List<Long>, blog: UserModel, model: Model): String {
        val pin = loadPin(model)
        val stories = loadMyStories(blog, pin, model, 50)
        loadFollowingStories(followingUserIds, model, 10)
        loadLatestStories(blog, followingUserIds, model)
        loadNextStep(blog, model)
        shouldShowFollowButton(blog, model)
        shouldShowCreateStory(blog, stories, model)
        return "page/blog/writer"
    }

    private fun loadReader(followingUserIds: List<Long>, blog: UserModel, model: Model): String {
        loadFollowingStories(followingUserIds, model, 50)
        loadLatestStories(blog, followingUserIds, model)
        return "page/blog/reader"
    }

    private fun loadMyStories(blog: UserModel, pin: PinModel?, model: Model, limit: Int): List<StoryModel> {
        val request = SearchStoryRequest(
                userIds = listOf(blog.id),
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.published,
                sortOrder = SortOrder.descending,
                limit = limit
        )
        val stories = storyService.search(request, pin)
        if (!stories.isEmpty()){
            model.addAttribute("myStories", pinStory(stories, pin?.storyId))
        }
        return stories
    }

    private fun pinStory(stories: List<StoryModel>, pinnedStoryId: Long?): List<StoryModel> {
        val pinnedStory = stories.find { it.id == pinnedStoryId }
                ?: return stories

        val result = mutableListOf<StoryModel>()
        result.add(pinnedStory)
        result.addAll(stories.filter { it.id != pinnedStory.id })
        return result
    }

    private fun loadFollowingStories(followingUserIds: List<Long>, model: Model, limit: Int) {
        // Find following users
        if (followingUserIds.isEmpty())
            return

        // Find stories from following users
        val stories = storyService.search(SearchStoryRequest(
                userIds = followingUserIds,
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.published,
                sortOrder = SortOrder.descending,
                limit = limit
        ))
        val followingStories = storyService.sort(
                stories = stories,
                algorithm = SortAlgorithmType.most_viewed,
                bubbleDownViewedStories = true,
                statsHoursOffset = 7 * 24
        )
        model.addAttribute("followingStories", followingStories)
    }

    private fun loadLatestStories(blog: UserModel, followingUserIds: List<Long>, model: Model) {
        val latestStories = LinkedHashMap<UserModel, StoryModel>()
        val stories = storyService.search(SearchStoryRequest(
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.published,
                sortOrder = SortOrder.descending,
                limit = 50
        ))
                .filter { it.user.id != blog.id && !followingUserIds.contains(it.user.id) }
                .forEach{
                    val user = it.user
                    if (!latestStories.containsKey(user)){
                        latestStories[user] = it
                    }
                }

        model.addAttribute("latestStories", latestStories.values.take(5))
    }

    private fun loadPin(model: Model): PinModel? {
        if (!requestContext.toggles().pin)
            return null

        val pin = pinService.get()
        model.addAttribute("pin", pin)
        return pin
    }

    private fun loadNextStep(blog: UserModel, model: Model) {
        if (!requestContext.toggles().nextAction || blog.id != requestContext.currentUser()?.id)
            return

        val nextAction = NextActionModel(
                biography = blog.biography.isNullOrEmpty(),
                twitter = channelService.all().filter { it.type == ChannelType.twitter && it.connected }.isEmpty(),
                newsletter = blog.newsletterDeliveryDayOfWeek <= 0
        )
        if (nextAction.biography || nextAction.twitter || nextAction.newsletter){
            model.addAttribute("nextAction", nextAction)
        }
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
