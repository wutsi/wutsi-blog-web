package com.wutsi.blog.app.page.login

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.web.savedrequest.SavedRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URL
import javax.servlet.http.HttpServletRequest



@Controller
@RequestMapping("/login")
class LoginController(
        @Value("\${wutsi.domain}") private val domain: String,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    @GetMapping()
    fun index(
            @RequestParam(required = false) error: String? = null,
            @RequestParam(required = false) reason: String? = null,
            @RequestParam(required = false) redirect: String? = null,
            @RequestParam(required = false) `return`: String? = null,
            @RequestHeader(required = false) referer: String? = null,
            model: Model,
            request: HttpServletRequest
    ): String {
        model.addAttribute("error", error)

        model.addAttribute("googleUrl", loginUrl("/login/google", redirect))
        model.addAttribute("facebookUrl", loginUrl("/login/facebook", redirect))
        model.addAttribute("githubUrl", loginUrl("/login/github", redirect))
        model.addAttribute("twitterUrl", loginUrl("/login/twitter", redirect))

        val createBlog = isCreateBlog(request)
        model.addAttribute("createBlog", createBlog)
        model.addAttribute("info", info(createBlog, reason))
        model.addAttribute("title", title(createBlog))
        model.addAttribute("return", `return`)

        return "page/login/index"
    }

    override fun pageName() = PageName.LOGIN

    private fun isCreateBlog(request: HttpServletRequest): Boolean {
        val savedRequest = request.session.getAttribute("SPRING_SECURITY_SAVED_REQUEST") as SavedRequest?
            ?: return false

        val url = URL(savedRequest.redirectUrl)
        return domain.equals(url.host) && url.file == "/create/name"
    }

    private fun loginUrl(url: String, redirectUrl: String?): String {
        return if (redirectUrl == null) url else "$url?redirect=$redirectUrl"
    }

    private fun title(createBlog: Boolean): String {
        val default = "page.login.header1.login";
        val key = if (createBlog) {
            "page.login.header1.create-blog"
        } else {
            default
        }

        return requestContext.getMessage(key, default)
    }

    private fun info(createBlog: Boolean, reason: String?): String {
        val default = "page.login.info.login";
        val key = if (createBlog) {
            "page.login.info.create-blog"
        } else if (reason == null) {
            default
        } else {
            "page.login.info.$reason"
        }

        return requestContext.getMessage(key, default)
    }
}
