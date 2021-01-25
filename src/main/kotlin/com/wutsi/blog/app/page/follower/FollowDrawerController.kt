package com.wutsi.blog.app.page.follower

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.ViewService
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
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
    private val requestContext: RequestContext
) {
    companion object {
        private val MAX_MONTHLY_FREE_VIEWED_STORIES = 3
        private val MAX_DAILY_IMPRESSION_COUNT = 3
    }

    @GetMapping()
    fun index(
        @RequestParam userId: Long,
        @RequestParam storyUrl: String,
        model: Model
    ): String {
        if (shouldShow(userId)) {
            val user = userService.get(userId)
            trackImpression()

            model.addAttribute("show", true)
            model.addAttribute("user", user)
            model.addAttribute("followUrl", "${user.slug}/follow")
            model.addAttribute("storyUrl", storyUrl)
        }
        return "page/follow/drawer"
    }

    private fun shouldShow(userId: Long): Boolean {
        if (
            impressionCount() >= MAX_DAILY_IMPRESSION_COUNT || /* Not too many impressions */
            !requestContext.toggles().followDrawer || /* Toggles enabled */
            !requestContext.toggles().follow ||
            !followService.canFollow(userId) /* user cannot follow the blog */
        )
            return false

        val storyIds = viewService.storiesViewedThisMonth()
        return storyIds.size >= MAX_MONTHLY_FREE_VIEWED_STORIES
    }

    fun impressionCount(): Int {
        val value = CookieHelper.get(impressionKey(), requestContext.request)
            ?: return 0
        try {
            return value.toInt()
        } catch (ex: Exception) {
            return 0
        }
    }

    private fun trackImpression() {
        CookieHelper.put(
            name = impressionKey(),
            value = (impressionCount() + 1).toString(),
            request = requestContext.request,
            response = requestContext.response,
            maxAge = CookieHelper.ONE_DAY_SECONDS
        )
    }

    private fun impressionKey(): String =
        CookieName.FOLLOW_DRAWER
}
