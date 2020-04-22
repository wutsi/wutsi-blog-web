package com.wutsi.blog.app.controller.welcome

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/welcome/email")
class WelcomeEmailController(
        userService: UserService,
        requestContext: RequestContext
): AbstractWelcomeController(userService, requestContext) {
    override fun pageName() = PageName.WELCOME_EMAIL

    override fun pagePath() = "page/welcome/email"

    override fun redirectUrl() = "/welcome/biography"

    override fun attributeName() = "email"

    override fun value() = requestContext.currentUser()?.email
}
