package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WriterController(
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.WRITER

    @GetMapping("/writer")
    fun index(model: Model): String {
        return "page/writer"
    }

}
