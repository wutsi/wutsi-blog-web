package com.wutsi.blog.app.controller.partner

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.service.PartnerService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping()
class PartnerJoinController(
        private val service: PartnerService,
        requestContext: RequestContext
): AbstractPageController( requestContext) {
    override fun pageName() = PageName.PARTNER_JOIN

    @GetMapping("/partner/join")
    fun index(model: Model): String {

        try {
            val partner = service.get()
            model.addAttribute("partner", partner)
        } catch (ex: Exception){

        } finally {
            return "page/partner/join"
        }
    }
}
