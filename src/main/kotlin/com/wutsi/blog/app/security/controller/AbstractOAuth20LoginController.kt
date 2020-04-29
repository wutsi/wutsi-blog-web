package com.wutsi.blog.app.security.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuth2AccessTokenErrorResponse
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Response
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import com.wutsi.blog.app.security.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import java.net.URLEncoder
import java.util.UUID
import javax.servlet.http.HttpServletRequest


abstract class AbstractOAuth20LoginController(
        protected val logger: KVLogger
) {
    protected val objectMapper = ObjectMapper()

    protected abstract fun getOAuthService() : OAuth20Service

    protected abstract fun getUserUrl(): String

    protected abstract fun toOAuthUser(attrs: Map<String, Any>): OAuthUser

    @GetMapping()
    fun login (request: HttpServletRequest): String {
        val url = getAuthorizationUrl(request)
        return "redirect:$url"
    }

    @GetMapping("/callback")
    open fun callback(request: HttpServletRequest): String {
        var url: String
        try {

            val error = getError(request)
            url = if (error == null) getSigninUrl(request) else errorUrl(error)

        } catch(ex: OAuth2AccessTokenErrorResponse) {

            url = errorUrl(ex.error.errorString)
            logger.add("Exception", ex.javaClass.name)
            logger.add("ExceptionMessage", ex.message)
            LoggerFactory.getLogger(javaClass).error("Failure", ex)
        }

        logger.add("RedirectURL", url)
        return "redirect:$url"
    }

    open fun toOAuthUser(accessToken: String): OAuthUser {
        val response = fetchUser(accessToken)
        logger.add("OAuthUser", response.body)

        val attrs = objectMapper.readValue(response.body, Map::class.java) as Map<String, Any>
        return toOAuthUser(attrs)
    }

    private fun fetchUser(accessToken: String): Response {
        val request = OAuthRequest(Verb.GET, getUserUrl())
        val oauth = getOAuthService()
        oauth.signRequest(accessToken, request)

        return oauth.execute(request)
    }


    private fun errorUrl(error: String) : String {
        return "login?error=" + URLEncoder.encode(error, "utf-8")
    }

    private fun getSigninUrl(request: HttpServletRequest): String {
        val code = getCode(request)
        val state = getState(request)
        val accessToken = getOAuthService().getAccessToken(code).accessToken
        val user = toOAuthUser(accessToken)

        return getSigninUrl(accessToken, state, user)
    }

    protected fun getSigninUrl(accessToken: String, state: String, user: OAuthUser) =
            SecurityConfiguration.OAUTH_SIGNIN_PATTERN +
                    "?" + SecurityConfiguration.PARAM_ACCESS_TOKEN + "=$accessToken" +
                    "&" + SecurityConfiguration.PARAM_STATE + "=$state" +
                    "&" + SecurityConfiguration.PARAM_USER + "=" + URLEncoder.encode(objectMapper.writeValueAsString(user), "utf-8")

    open fun getState(request: HttpServletRequest) = request.getParameter("state")

    open fun getCode(request: HttpServletRequest) = request.getParameter("code")

    open fun getError(request: HttpServletRequest) = request.getParameter("error")


    private fun getAuthorizationUrl (request: HttpServletRequest): String {
        val state = generateState(request)
        return getOAuthService().getAuthorizationUrl(state)
    }

    protected fun generateState(request: HttpServletRequest): String {
        val state = UUID.randomUUID().toString()
        request.session.setAttribute(SecurityConfiguration.SESSION_STATE, state)
        return state
    }
}
