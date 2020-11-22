package com.wutsi.blog.app.page.create

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.page.settings.model.UserAttributeForm
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

abstract class AbstractCreateController(
        protected val userService: UserService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
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
