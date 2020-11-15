package com.wutsi.blog.app.page.blog

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.schemas.PersonSchemasGenerator
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.user.SearchUserRequest
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
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.BLOG

    override fun shouldBeIndexedByBots() = true

    @GetMapping("/@/{name}")
    fun index(@PathVariable name: String, model: Model): String {
        val blog = userService.get(name)

        model.addAttribute("blog", blog)
        model.addAttribute("page", getPage(blog))

        val followingUserIds = followerService.searchFollowingUserIds()
                .filter { it != blog.id }

        loadMyStories(blog, model)
        loadWhoToFollow(blog, followingUserIds, model)
        loadFollowingStories(followingUserIds, model)
        shouldShowFollowButton(blog, followingUserIds, model)
        return "page/blog/index"
    }

    private fun loadMyStories(blog: UserModel, model: Model) {
        if (!blog.blog){
            return
        }

        val stories = storyService.search(SearchStoryRequest(
                userIds = listOf(blog.id),
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.published,
                sortOrder = SortOrder.descending
        ))
        if (!stories.isEmpty()){
            model.addAttribute("myStories", stories)
        }
    }

    private fun loadWhoToFollow(blog: UserModel, followingUserIds: List<Long>, model: Model) {
        val blogs = userService.search(SearchUserRequest(
                blog = true,
                limit = 10
        ))
                .filter { it.id != blog.id && !followingUserIds.contains(it.id) }
                .shuffled()
                .take(3)

        model.addAttribute("whoToFollow", blogs)
    }

    private fun loadFollowingStories(followingUserIds: List<Long>, model: Model) {
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
                limit = 10
        ))
        val sortedStories = storyService.sort(
                stories = stories,
                algorithm = SortAlgorithmType.most_viewed,
                bubbleDownViewedStories = true,
                statsHoursOffset = 7 * 24
        )
        model.addAttribute("followingStories", sortedStories)
    }

    private fun shouldShowFollowButton(blog: UserModel, followingUserIds: List<Long>, model: Model) {
        val showButton = requestContext.toggles().follow
                && (requestContext.currentUser() == null || followingUserIds.contains(blog.id))
        model.addAttribute("showFollowButton", showButton)
    }



    protected fun getPage(user: UserModel) = createPage(
            name = pageName(),
            title = user.fullName,
            description = if (user.biography == null) "" else user.biography,
            type = "profile",
            url = url(user),
            imageUrl = user.pictureUrl,
            schemas = schemas.generate(user)
    )
}
