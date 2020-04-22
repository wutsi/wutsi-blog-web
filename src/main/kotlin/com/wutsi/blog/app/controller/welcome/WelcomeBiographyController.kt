package com.wutsi.blog.app.controller.welcome

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/welcome/biography")
class WelcomeBiographyController(
        userService: UserService,
        requestContext: RequestContext
): AbstractWelcomeController(userService, requestContext) {
    override fun pageName() = PageName.WELCOME_BIOGRAPHY

    override fun pagePath() = "page/welcome/biography"

    override fun redirectUrl() = "/welcome/picture"

    override fun attributeName() = "biography"

    override fun value() = requestContext.currentUser()?.biography
}
