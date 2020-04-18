package com.wutsi.blog.app.security.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuth2AccessTokenErrorResponse
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Response
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.security.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import java.net.URLEncoder
import java.util.UUID
import javax.servlet.http.HttpServletRequest


abstract class AbstractOAuth20LoginController {
    protected val objectMapper = ObjectMapper()

    protected val logger = LoggerFactory.getLogger(this::class.java)

    protected abstract fun getOAuthService() : OAuth20Service

    protected abstract fun getUserUrl(): String

    protected abstract fun toOAuthUser(attrs: Map<String, Any>): OAuthUser

    @GetMapping()
    fun login (request: HttpServletRequest): String {
        val url = getAuthorizationUrl(request)
        return "redirect:$url"
    }

    @GetMapping("/callback")
    fun callback(request: HttpServletRequest): String {
        try {
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
            LoggerFactory.getLogger(javaClass).info("Redirecting to $url")
            return "redirect:$url"
        } catch(ex: OAuth2AccessTokenErrorResponse) {
            logger.info("Authentication error", ex)
            return redirectUrl(ex.error.errorString)
        }
    }

    protected fun loadUser(accessToken: String): OAuthUser {
        val response = fetchUser(accessToken)
        logger.info("OAuth User: " + response.body)

        val attrs = objectMapper.readValue(response.body, Map::class.java) as Map<String, Any>
        return toOAuthUser(attrs)
    }

    private fun fetchUser(accessToken: String): Response {
        val request = OAuthRequest(Verb.GET, getUserUrl())
        val oauth = getOAuthService()
        oauth.signRequest(accessToken, request)

        return oauth.execute(request)
    }


    private fun redirectUrl(error: String) : String {
        return "redirect:/login?error=" + URLEncoder.encode(error, "utf-8")
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
                "&" + SecurityConfiguration.PARAM_USER + "=" + URLEncoder.encode(objectMapper.writeValueAsString(user), "utf-8")

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
