package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class JoinController(
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.JOIN

    @GetMapping("/join")
    fun index(model: Model): String {
        return "redirect:/welcome"
        //return "page/join"
    }

}
