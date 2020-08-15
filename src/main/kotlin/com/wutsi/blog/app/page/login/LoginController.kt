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
            @RequestHeader(required = false) referer: String? = null,
            model: Model,
            request: HttpServletRequest
    ): String {
        model.addAttribute("error", error)
        model.addAttribute("createBlog", isCreateBlog(request))

        val redirect = request.getParameter("redirect")
        model.addAttribute("googleUrl", loginUrl("/login/google", redirect))
        model.addAttribute("facebookUrl", loginUrl("/login/facebook", redirect))
        model.addAttribute("githubUrl", loginUrl("/login/github", redirect))
        model.addAttribute("twitterUrl", loginUrl("/login/twitter", redirect))

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
}
