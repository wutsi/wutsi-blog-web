package com.wutsi.blog.app.controller.login

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URL

@Controller
@RequestMapping("/login")
class LoginController(
        @Value("\${wutsi.domain}") private val domain: String,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LoginController::class.java)
    }

    @GetMapping()
    fun index(
            @RequestParam(required = false) error: String? = null,
            @RequestHeader(required = false) referer: String? = null,
            model: Model
    ): String {
        model.addAttribute("error", error)
        model.addAttribute("join", isJoin(referer))
        return "page/login"
    }

    override fun pageName() = PageName.LOGIN

    private fun isJoin(referer: String?): Boolean {
        if (referer == null) {
            return false
        }

        try {
            val url = URL(referer)
            return domain.equals(url.host) && url.file == "/join"
        } catch (ex: Exception) {
            LOGGER.warn("Invalid URL: $referer", ex)
            return false
        }
    }
}
