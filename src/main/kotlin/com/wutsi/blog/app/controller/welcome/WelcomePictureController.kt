package com.wutsi.blog.app.controller.welcome

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/welcome/picture")
class WelcomePictureController(
        userService: UserService,
        requestContext: RequestContext
): AbstractWelcomeController(userService, requestContext) {
    override fun pageName() = PageName.WELCOME_PICTURE

    override fun pagePath() = "page/welcome/picture"

    override fun redirectUrl() = "/welcome/success"

    override fun attributeName() = "pictureurl"

    override fun value() = requestContext.currentUser()?.pictureUrl
}
