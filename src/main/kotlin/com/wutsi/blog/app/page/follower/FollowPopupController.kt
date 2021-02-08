package com.wutsi.blog.app.page.follower

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import com.wutsi.core.logging.KVLogger
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/follow/popup")
class FollowPopupController(
    private val followService: FollowerService,
    private val userService: UserService,
    private val requestContext: RequestContext,
    private val logger: KVLogger
) {
    companion object {
        private val MAX_DAILY_IMPRESSION_COUNT = 3
    }

    @GetMapping()
    fun index(
        @RequestParam userId: Long,
        @RequestParam storyUrl: String,
        model: Model
    ): String {
        if (shouldShow(userId)) {
            val blog = userService.get(userId)
            trackImpression()

            model.addAttribute("show", true)
            model.addAttribute("blog", blog)
            model.addAttribute("user", requestContext.currentUser())
            model.addAttribute("followUrl", "${blog.slug}/follow")
            model.addAttribute("storyUrl", storyUrl)

            logger.add("ShowPopup", true)
        } else {
            logger.add("ShowPopup", false)
        }
        return "page/follow/popup"
    }

    private fun shouldShow(userId: Long): Boolean =
        evaluateRuleToggle() &&
            evaluateRuleImpressionCount() &&
            evaluateRuleUserNotAnonymous() &&
            evaluateRuleUserNotFollower(userId)

    private fun evaluateRuleToggle(): Boolean {
        val value = requestContext.toggles().followPopup &&
            requestContext.toggles().follow
        logger.add("RuleToggle", value)
        return value
    }

    private fun evaluateRuleImpressionCount(): Boolean {
        val count = impressionCount()
        val value = count <= MAX_DAILY_IMPRESSION_COUNT
        logger.add("ImpressionCount", count)
        logger.add("RuleImpressionCount", value)
        return value
    }

    private fun impressionCount(): Int {
        val value = CookieHelper.get(impressionKey(), requestContext.request)
            ?: return 0
        try {
            return value.toInt()
        } catch (ex: Exception) {
            return 0
        }
    }

    private fun evaluateRuleUserNotAnonymous(): Boolean {
        val value = requestContext.currentUser() != null
        logger.add("RuleUserNotAnonymous", value)
        return value
    }

    private fun evaluateRuleUserNotFollower(userId: Long): Boolean {
        val value = followService.canFollow(userId)
        logger.add("RuleUserNotFollower", value)
        return value
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
        CookieName.FOLLOW_POPUP
}
