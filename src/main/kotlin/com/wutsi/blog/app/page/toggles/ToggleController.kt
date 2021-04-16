package com.wutsi.blog.app.page.toggles

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.TogglesHolder
import com.wutsi.blog.app.util.CookieHelper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/toggles")
class ToggleController(
    private val requestContext: RequestContext
) {
    @GetMapping("/set", produces = ["text/plain"])
    fun index(
        @RequestParam name: String,
        @RequestParam(required = false) value: String? = null
    ): String {
        if (name == TogglesHolder.COOKIE_FACEBOOK) {
            setCookie(name, value)
        }
        return "OK"
    }

    private fun setCookie(name: String, value: String?) {
        if (value.isNullOrEmpty())
            CookieHelper.remove(name, requestContext.response)
        else
            CookieHelper.put(name, value, requestContext.request, requestContext.response)
    }
}
