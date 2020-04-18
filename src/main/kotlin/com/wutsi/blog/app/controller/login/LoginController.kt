package com.wutsi.blog.app.controller.login

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/login")
class LoginController(requestContext: RequestContext): AbstractPageController(requestContext) {
    @GetMapping()
    fun index(
            @RequestParam(required = false) error: String? = null,
            model: Model
    ): String {
        model.addAttribute("error", error)
        return "page/login"
    }

    override fun pageName() = PageName.LOGIN
}
