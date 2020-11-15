package com.wutsi.blog.app.page.login

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
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
        private val userService: UserService,
        @Value("\${wutsi.domain}") private val domain: String,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    companion object{
        const val REASON_CREATE_BLOG = "create-blog"
        const val REASON_FOLLOW = "follow"
    }

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

        val xreason = getReason(reason, request)
        model.addAttribute("createBlog", xreason == REASON_CREATE_BLOG)
        model.addAttribute("info", info(xreason))
        model.addAttribute("title", title(xreason))
        model.addAttribute("return", `return`)

        model.addAttribute("googleUrl", loginUrl("/login/google", redirect))
        model.addAttribute("facebookUrl", loginUrl("/login/facebook", redirect))
        model.addAttribute("githubUrl", loginUrl("/login/github", redirect))
        model.addAttribute("twitterUrl", loginUrl("/login/twitter", redirect))

        return "page/login/index"
    }

    override fun pageName() = PageName.LOGIN

    private fun getReason(reason: String?, request: HttpServletRequest): String? {
        if (reason != null){
            return reason
        }

        val savedRequest = request.session.getAttribute("SPRING_SECURITY_SAVED_REQUEST") as SavedRequest?
                ?: return null
        val url = URL(savedRequest.redirectUrl)
        if (domain.equals(url.host)){
            if (url.path == "/create/name"){
                return REASON_CREATE_BLOG
            } else if (url.path == "/follow") {
                return REASON_FOLLOW
            }
        }
        return null
    }

    private fun loginUrl(url: String, redirectUrl: String?): String {
        return if (redirectUrl == null) url else "$url?redirect=$redirectUrl"
    }

    private fun title(reason: String?): String {
        val default = "page.login.header1.login";
        val key = if (reason != null) {
            "page.login.header1.$reason"
        } else {
            default
        }

        return requestContext.getMessage(key, default)
    }

    private fun info(reason: String?): String {
        val default = "page.login.info.login";
        val key = if (reason != null) {
            "page.login.info.$reason"
        } else {
            default
        }

        return requestContext.getMessage(key, default)
    }

    private fun loadUser(id: Long?): UserModel? {
        id ?: return null

        try {
            return userService.get(id)
        } catch (ex: Exception){
            return null
        }
    }
}
