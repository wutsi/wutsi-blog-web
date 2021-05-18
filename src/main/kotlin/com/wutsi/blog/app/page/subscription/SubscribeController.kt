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
        val redirectUrl = blog.slug
        model.addAttribute("redirectUrl", redirectUrl)

        // Paid Subscription
        var result = paid(blog, model)

        // Free Subscription
        if (result == null)
            result = free(blog, model, redirectUrl)

        return result
    }

    private fun paid(blog: UserModel, model: Model): String? {
        val plan = monetizationService.currentPlan(blog.id) ?: return null
        model.addAttribute("plan", plan)

        val subscription = monetizationService.currentSubscription(blog.id)
        if (subscription != null)
            return "redirect:/@/${blog.name}" // Already subscribed - Goto home base

        val site = requestContext.site()
        val paypalPaymentEnabled = isPaypalPaymentEnabled(site)
        if (paypalPaymentEnabled) {
            model.addAttribute("showPaypalButton", paypalPaymentEnabled)
            model.addAttribute("checkoutPaypalUrl", "/checkout/paypal?planId=${plan.id}")
        }

        val mobilePaymentEnabled = false
        if (mobilePaymentEnabled) {
            model.addAttribute("showMobileButton", mobilePaymentEnabled)
            model.addAttribute("checkoutMobileUrl", "/checkout/mobile?planId=${plan.id}")
        }
        return "page/subscription/paid"
    }

    private fun free(blog: UserModel, model: Model, redirectUrl: String): String {
        // User cannot follow the flog
        if (!followerService.canFollow(blog.id))
            return "redirect:${blog.slug}"

        // Follow the blog
        model.addAttribute("redirectUrl", redirectUrl)
        model.addAttribute("googleUrl", authenticationService.loginUrl("/login/google", redirectUrl))
        return "page/subscription/free"
    }

    private fun isPaypalPaymentEnabled(site: Site): Boolean =
        site.attributes.find { it.urn == SiteAttribute.PAYPAL_ENABLED.urn }?.value == "true"
}
