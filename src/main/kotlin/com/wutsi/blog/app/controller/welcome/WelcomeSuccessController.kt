package com.wutsi.blog.app.controller.welcome

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/welcome/success")
class WelcomeSuccessController(
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.WELCOME_SUCCESS

    @GetMapping
    fun index(): String {
        return "page/welcome/success"
    }

}
