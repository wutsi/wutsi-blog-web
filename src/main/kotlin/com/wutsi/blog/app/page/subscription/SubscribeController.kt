package com.wutsi.blog.app.page.subscription

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
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
        val blog = userService.get(name)
        if (!blog.blog) {
            LOGGER.warn("$name is not a Blog. Redirecting to his home page")
            return "redirect:/@/${blog.name}"
        }

        // Subscription
        val plan = monetizationService.currentPlan(blog.id)
        if (plan != null) {
            val subscription = monetizationService.currentSubscription(blog.id)
            if (subscription != null) {
                LOGGER.info("$name is subscribed to Blog#${blog.id}")
                return "redirect:/@/${blog.name}"
            } else {
                LOGGER.info("$name has no subscription")

                val site = requestContext.site()
                val paypalPaymentEnabled = isPaypalPaymentEnabled(site)
                if (paypalPaymentEnabled) {
                    model.addAttribute("paypalPaymentEnabled", paypalPaymentEnabled)
                    model.addAttribute("checkoutPaypalUrl", "/checkout/paypal?planId=${plan.id}")
                }

                val mobilePaymentEnabled = false
                if (mobilePaymentEnabled) {
                    model.addAttribute("mobilePaymentEnabled", mobilePaymentEnabled)
                    model.addAttribute("checkoutMobileUrl", "/checkout/mobile?planId=${plan.id}")
                }
            }
        } else {
            return follow(blog, model)
        }

        // User
        val user = requestContext.currentUser() ?: return follow(blog, model)

        model.addAttribute("user", user)
        model.addAttribute("blog", blog)
        model.addAttribute("plan", plan)
        if (error != null)
            model.addAttribute("error", requestContext.getMessage(error))

        return "page/subscription/subscribe"
    }

    private fun follow(blog: UserModel, model: Model): String {
        return "redirect:/@/${blog.name}"
    }

    private fun isPaypalPaymentEnabled(site: Site): Boolean =
        site.attributes.find { it.urn == SiteAttribute.PAYPAL_ENABLED.urn }?.value == "true"
}
