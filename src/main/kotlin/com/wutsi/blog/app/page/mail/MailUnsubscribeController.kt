package com.wutsi.blog.app.page.mail

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.page.mail.service.MailService
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class MailUnsubscribeController(
        private val service: MailService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.MAIL_UNSUBSCRIBE

    @GetMapping("/mail/unsubscribe")
    fun unsubscribe(@RequestParam email: String, model: Model): String {
        service.unsubscribe(email)
        model.addAttribute("email", email)
        return "page/mail/unsubscribe"
    }

    @PostMapping("/mail/unsubscribe")
    fun postUnsubscribe(@RequestParam email: String, model: Model): String {
        return unsubscribe(email, model)
    }
}
