package com.wutsi.blog.app.page.subscription

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.login.service.AuthenticationService
import com.wutsi.blog.app.page.monetization.service.MonetizationService
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
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
            LOGGER.warn("User#$name is not a Blog. Redirecting to his home page")
            return "redirect:/@/${blog.name}"
        }

        // Error
        if (error != null)
            model.addAttribute("error", requestContext.getMessage(error))

        // Subscription
        val plan = monetizationService.currentPlan(blog.id)
        if (plan != null) {
            val subscription = monetizationService.currentSubscription(blog.id)
            if (subscription != null) {
                LOGGER.info("User is subscribed to Blog#${blog.id}")
                return "redirect:/@/${blog.name}"
            } else {
                model.addAttribute("checkoutPaypalUrl", "/checkout/paypal?planId=${plan.id}")
                model.addAttribute("checkoutMobileUrl", "/checkout/mobile?planId=${plan.id}")
            }
        }

        // User
        val user = requestContext.currentUser()
        if (user == null) {
            val redirectUrl = requestContext.request.requestURL.toString()
            model.addAttribute("googleLoginUrl", authenticationService.loginUrl("/login/google", redirectUrl))
        } else {
            // Check of the user already follow the blog
            // TODO
        }

        model.addAttribute("user", user)
        model.addAttribute("blog", blog)
        model.addAttribute("plan", plan)

        return "page/subscription/subscribe"
    }
}
