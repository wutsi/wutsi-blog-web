package com.wutsi.blog.app.page.partner

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping()
@ConditionalOnProperty(value="wutsi.toggles.wpp", havingValue = "true")
class PartnerController(
        requestContext: RequestContext
): AbstractPageController( requestContext) {
    override fun pageName() = PageName.PARTNER

    override fun shouldBeIndexedByBots() = true

    @GetMapping("/partner")
    fun index(model: Model): String {
        return "page/partner/index"
    }

    override fun page() = createPage(
            title = "Wutsi Partner Program",
            description = requestContext.getMessage("page.partner.description")
    )

}
