package com.wutsi.blog.app.security.oauth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.security.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import org.springframework.web.bind.annotation.GetMapping
import java.net.URLEncoder
import java.util.UUID
import javax.servlet.http.HttpServletRequest



abstract class AbstractOAuth20LoginController {
    protected val objectMapper = ObjectMapper()

    protected abstract fun loadUser(accessToken: String): OAuthUser

    protected abstract fun getOAuthService() : OAuth20Service


    @GetMapping()
    fun login (request: HttpServletRequest): String {
        val url = getAuthorizationUrl(request)
        return "redirect:$url"
    }

    @GetMapping("/callback")
    fun callback(request: HttpServletRequest): String {
        val error = getError(request)
        if (error != null) {
            return "redirect:/login?error=" + URLEncoder.encode(error, "utf-8")
        }

        val code = getCode(request)
        val state = getState(request)
        val accessToken = getOAuthService().getAccessToken(code).accessToken
        val url = getSigninUrl(
                accessToken = accessToken,
                state = state,
                user = loadUser(accessToken)
        )
        return return "redirect:$url"
    }

    protected fun getSigninUrl(
            accessToken: String,
            state: String,
            user: OAuthUser
    ): String {
        return SecurityConfiguration.OAUTH_SIGNIN_PATTERN +
                "?" + SecurityConfiguration.PARAM_ACCESS_TOKEN + "=$accessToken" +
                "&" + SecurityConfiguration.PARAM_STATE + "=$state" +
                "&" + SecurityConfiguration.PARAM_PROVIDER + "=" + SecurityConfiguration.PROVIDER_GITHUB +
                "&" + SecurityConfiguration.PARAM_USER + "=" + objectMapper.writeValueAsString(user)

    }


    open fun getState(request: HttpServletRequest) = request.getParameter("state")

    open fun getCode(request: HttpServletRequest) = request.getParameter("code")

    open fun getError(request: HttpServletRequest) = request.getParameter("error")


    private fun getAuthorizationUrl (request: HttpServletRequest): String {
        val state = generateState(request)
        return getOAuthService().getAuthorizationUrl(state)
    }

    private fun generateState(request: HttpServletRequest): String {
        val state = UUID.randomUUID().toString()
        request.session.setAttribute(SecurityConfiguration.SESSION_STATE, state)
        return state
    }
}
