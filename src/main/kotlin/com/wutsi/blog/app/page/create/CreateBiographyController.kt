package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create/biography")
class CreateBiographyController(
        userService: UserService,
        requestContext: RequestContext
): AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE_BIOGRAPHY

    override fun pagePath() = "page/create/biography"

    override fun redirectUrl() = "/create/picture"

    override fun attributeName() = "biography"

    override fun value() = requestContext.currentUser()?.biography
}
