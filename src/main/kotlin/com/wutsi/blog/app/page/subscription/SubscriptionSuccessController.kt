package com.wutsi.blog.app.page.subscription

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class SubscriptionSuccessController(
    private val userService: UserService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SUBSCRIPTION_SUCCESS

    @GetMapping("/@/{name}/subscribe/success")
    fun index(@PathVariable name: String, model: Model): String {
        val blog = userService.get(name)

        model.addAttribute("blog", blog)
        return "page/subscription/success"
    }
}
