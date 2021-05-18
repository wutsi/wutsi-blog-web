package com.wutsi.blog.app.page.subscription

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.login.service.AuthenticationService
import com.wutsi.blog.app.page.monetization.service.MonetizationService
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.site.SiteAttribute
import com.wutsi.site.dto.Site
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SubscribeController(
    private val userService: UserService,
    private val monetizationService: MonetizationService,
    private val authenticationService: AuthenticationService,
    private val followerService: FollowerService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubscribeController::class.java)
    }

    override fun pageName() = PageName.SUBSCRIPTION

    @GetMapping("/@/{name}/subscribe")
    fun index(
        @PathVariable name: String,
        @RequestParam(required = false) error: String? = null,
        @RequestParam(required = false) premium: String? = null,
        @RequestParam(required = false) redirect: String? = null,
        model: Model
    ): String {
        // Error
        if (error != null)
            model.addAttribute("error", requestContext.getMessage(error))

        // Blog
        val blog = userService.get(name)
        if (!blog.blog) {
            LOGGER.warn("$name is not a Blog. Redirecting to his home page")
            return "redirect:${blog.slug}"
        }
        model.addAttribute("blog", blog)
        model.addAttribute("user", requestContext.currentUser())

        // Redirect Url
        val redirectUrl = redirect ?: blog.slug
        model.addAttribute("redirectUrl", redirectUrl)

        // Paid Subscription
        if (premium == "1") {
            val result = premium(blog, model)
            return if (result == null)
                return free(blog, model, redirectUrl)
            else
                return result
        } else {
            return free(blog, model, redirectUrl)
        }
    }

    @GetMapping("/@/{name}/subscribe/join")
    fun join(
        @PathVariable name: String,
        @RequestParam(required = false) redirectUrl: String? = null
    ): String {
        val blog = userService.get(name)
        if (!blog.blog) {
            LOGGER.warn("$name is not a Blog. Redirecting to his home page")
            return "redirect:${blog.slug}"
        }

        try {
            followerService.follow(blog.id)

            return if (redirectUrl == null)
                "redirect:${blog.slug}"
            else
                "redirect:$redirectUrl"
        } catch (ex: Exception) {
            LOGGER.error("Unable to follow the blog", ex)
            return "redirect:${blog.slug}/subscribe?error=error.unexpected"
        }
    }

    private fun premium(blog: UserModel, model: Model): String? {
        if (!requestContext.toggles().monetization) {
            LOGGER.warn("Monetization is not enabled")
            return null
        }

        val plan = monetizationService.currentPlan(blog.id) ?: return null
        model.addAttribute("plan", plan)

        val subscription = monetizationService.currentSubscription(blog.id)
        if (subscription != null)
            return "redirect:/@/${blog.name}?subscribed=1&premium=1" // Already subscribed - Goto home base

        val site = requestContext.site()
        val paypalPaymentEnabled = isPaypalPaymentEnabled(site)
        if (paypalPaymentEnabled) {
            model.addAttribute("showPaypalButton", paypalPaymentEnabled)
            model.addAttribute("checkoutPaypalUrl", "/checkout/paypal?planId=${plan.id}")
        }

        val mobilePaymentEnabled = isMobilePaymentEnabled(site)
        if (mobilePaymentEnabled) {
            model.addAttribute("showMobileButton", mobilePaymentEnabled)
            model.addAttribute("checkoutMobileUrl", "/checkout/mobile?planId=${plan.id}")
        }
        return "page/subscription/premium"
    }

    private fun free(blog: UserModel, model: Model, redirectUrl: String): String {
        // User cannot follow the flog
        val user = requestContext.currentUser()
        if (user != null && followerService.isFollowing(blog.id))
            return "redirect:${blog.slug}?subscribed=1"

        // Follow the blog
        val joinUrl = "/@/${blog.name}/subscribe/join?redirectUrl=$redirectUrl"
        model.addAttribute("redirectUrl", redirectUrl)
        model.addAttribute("joinUrl", joinUrl)
        model.addAttribute("googleUrl", authenticationService.loginUrl("/login/google", joinUrl))
        return "page/subscription/free"
    }

    private fun isPaypalPaymentEnabled(site: Site): Boolean =
        site.attributes.find { it.urn == SiteAttribute.PAYPAL_ENABLED.urn }?.value == "true"

    private fun isMobilePaymentEnabled(site: Site): Boolean =
        false
}
