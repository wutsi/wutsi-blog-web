package com.wutsi.blog.app.controller.user

import com.wutsi.blog.app.service.RequestContext
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MeController(
        private val requestContext: RequestContext
) {
    @GetMapping("/me")
    fun index(model: Model): String {
        val user = requestContext.currentUser()
        return "redirect:${user?.slug}"
    }
}
