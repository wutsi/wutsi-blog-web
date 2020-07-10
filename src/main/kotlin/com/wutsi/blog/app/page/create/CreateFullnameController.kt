package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create/fullname")
class CreateFullnameController(
        userService: UserService,
        requestContext: RequestContext
): AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE_FULLNAME

    override fun pagePath() = "page/create/fullname"

    override fun redirectUrl() = "/create/email"

    override fun attributeName() = "fullname"

    override fun value() = requestContext.currentUser()?.fullName
}
