package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create/picture")
class CreatePictureController(
        userService: UserService,
        requestContext: RequestContext
): AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE_PICTURE

    override fun pagePath() = "page/create/picture"

    override fun redirectUrl() = "/create/success"

    override fun attributeName() = "picture_url"

    override fun value() = requestContext.currentUser()?.pictureUrl
}
