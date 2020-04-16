package com.wutsi.blog.app.controller.author

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthorWelcomeController(
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.AUTHOR_WELCOME

    @GetMapping("/me/welcome")
    fun index(model: Model): String {
        return "page/author/welcome"
    }

}
