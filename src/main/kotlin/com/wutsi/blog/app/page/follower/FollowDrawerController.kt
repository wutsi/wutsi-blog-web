package com.wutsi.blog.app.page.follower

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.ViewService
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/follow/drawer")
class FollowDrawerController(
    private val viewService: ViewService,
    private val followService: FollowerService,
    private val userService: UserService,
    private val storyService: StoryService,
    private val requestContext: RequestContext
) {
    companion object {
        private val MAX_MONTHLY_FREE_VIEWED_STORIES = 3
    }

    @GetMapping()
    fun index(
        @RequestParam userId: Long,
        @RequestParam storyId: Long,
        model: Model
    ): String {
        val story = storyService.get(storyId)
        if (shouldShow(userId, story)) {
            val user = userService.get(userId)
            model.addAttribute("show", true)
            model.addAttribute("user", user)
            model.addAttribute("followUrl", "${user.slug}/follow")
            model.addAttribute("storyUrl", story.slug)
        }
        return "page/follow/drawer"
    }

    private fun shouldShow(userId: Long, story: StoryModel): Boolean {
        if (
            !requestContext.toggles().followDrawer ||
            !requestContext.toggles().follow ||
            !followService.canFollow(userId)
        )
            return false

        val storyIds = viewService.storiesViewedThisMonth()
        return storyIds.size >= MAX_MONTHLY_FREE_VIEWED_STORIES
    }
}
