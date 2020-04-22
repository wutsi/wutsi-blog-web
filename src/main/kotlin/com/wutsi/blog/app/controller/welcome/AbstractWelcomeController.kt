package com.wutsi.blog.app.controller.welcome

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.model.UserAttributeForm
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

abstract class AbstractWelcomeController(
        private val userService: UserService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.WELCOME_FULLNAME

    abstract fun pagePath(): String

    abstract fun redirectUrl(): String

    abstract fun attributeName(): String

    abstract fun value(): String?

    @GetMapping
    open fun index(model: Model): String {
        val value = value()
        model.addAttribute("value", value)
        return pagePath()
    }

    @GetMapping("/submit")
    fun submit(@RequestParam value: String, model: Model): String {
        try {

            userService.set(UserAttributeForm(
                    name = attributeName(),
                    value = value
            ))
            return "redirect:"  + redirectUrl()

        } catch (ex: Exception) {
            val error = errorKey(ex)
            model.addAttribute("error", requestContext.getMessage(error))
            model.addAttribute("value", value)
            return pagePath()
        }
    }
}
