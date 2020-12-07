package com.wutsi.blog.app.page.mail

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.page.mail.service.NewsletterService
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class MailController(
        private val newsletterService: NewsletterService,
        private val userService: UserService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.MAIL_UNSUBSCRIBE

    @GetMapping("/mail/unsubscribe")
    fun unsubscribe(
            @RequestParam email: String,
            @RequestParam(required=false, name = "u") userId: Long?= null,
            model: Model
    ): String {
        newsletterService.unsubscribe(email, userId)

        val blog = userId?.let{ userService.get(userId) } ?: null
        model.addAttribute("blog", blog)
        model.addAttribute("email", email)
        model.addAttribute("title", getTitle(blog))
        model.addAttribute("description", getDescription(blog))

        return "page/mail/unsubscribe"
    }

    @PostMapping("/mail/unsubscribe")
    fun _unsubscribe(
            @RequestParam email: String,
            @RequestParam(required=false, name = "u") userId: Long?= null,
            model: Model
    ): String {
        return unsubscribe(email, userId, model)
    }

    private fun getTitle(blog: UserModel?): String =
        if (blog == null){
            requestContext.getMessage("page.unsubscribe.site.title")
        } else {
            requestContext.getMessage("page.unsubscribe.blog.title")
        }

    private fun getDescription(blog: UserModel?): String =
            if (blog == null){
                requestContext.getMessage("page.unsubscribe.site.description")
            } else {
                requestContext.getMessage("page.unsubscribe.blog.description")
            }

}
