package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/login")
class LoginController(requestContext: RequestContext): AbstractPageController(requestContext) {
    @GetMapping()
    fun index(model: Model): String {
        return "page/login"
    }

    override fun page() = PageName.LOGIN
}
