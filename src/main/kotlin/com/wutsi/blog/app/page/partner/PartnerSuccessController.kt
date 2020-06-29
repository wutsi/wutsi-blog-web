package com.wutsi.blog.app.page.partner

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.page.partner.service.PartnerService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping()
class PartnerSuccessController(
        private val service: PartnerService,
        requestContext: RequestContext
): AbstractPageController( requestContext) {
    override fun pageName() = PageName.PARTNER_SUCCESS

    @GetMapping("/partner/success")
    fun index(model: Model): String {
        val partner = service.get()
        model.addAttribute("partner", partner)
        return "page/partner/success"
    }
}
