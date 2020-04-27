package com.wutsi.blog.app.controller.welcome

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/welcome")
class WelcomeController(
        userService: UserService,
        requestContext: RequestContext
): AbstractWelcomeController(userService, requestContext) {
    override fun pageName() = PageName.WELCOME

    override fun pagePath() = "page/welcome/index"

    override fun redirectUrl() = "/welcome/fullname"

    override fun attributeName() = "name"

    override fun value() = requestContext.currentUser()?.name

    @GetMapping
    override fun index(model: Model): String {
        val user = requestContext.currentUser()!!
        if (user.loginCount > 1) {
            // User has already a blog!
            return "redirect:" + user.slug
        }

        return super.index(model)
    }

}
