package com.wutsi.blog.app.controller.welcome

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/welcome/fullname")
class WelcomeFullnameController(
        userService: UserService,
        requestContext: RequestContext
): AbstractWelcomeController(userService, requestContext) {
    override fun pageName() = PageName.WELCOME_FULLNAME

    override fun pagePath() = "page/welcome/fullname"

    override fun redirectUrl() = "/welcome/email"

    override fun attributeName() = "fullname"

    override fun value() = requestContext.currentUser()?.fullName
}
