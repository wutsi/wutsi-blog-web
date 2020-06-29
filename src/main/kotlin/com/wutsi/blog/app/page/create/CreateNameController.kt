package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/create/name")
class CreateNameController(
        userService: UserService,
        requestContext: RequestContext
): AbstractCreateController(userService, requestContext) {
    override fun pageName() = PageName.CREATE_NAME

    override fun pagePath() = "page/create/name"

    override fun redirectUrl() = "/create/fullname"

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
