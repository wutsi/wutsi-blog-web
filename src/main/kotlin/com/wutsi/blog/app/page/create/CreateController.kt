package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping()
class CreateController(
        requestContext: RequestContext
): AbstractPageController( requestContext) {
    override fun pageName() = PageName.CREATE

    override fun shouldBeIndexedByBots() = true

    @GetMapping("/create")
    fun index(model: Model): String {
        return "page/create/index"
    }

    override fun page() = createPage(
            title = requestContext.getMessage("page.create.title"),
            description = requestContext.getMessage("page.create.description")
    )

}
