package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.security.config.SecurityConfiguration
import com.wutsi.blog.app.security.oauth.OAuthUser
import com.wutsi.core.logging.KVLogger
import java.net.URLEncoder
import java.util.UUID
import javax.servlet.http.HttpServletRequest


abstract class AbstractLoginController(
        protected val logger: KVLogger,
        protected val objectMapper: ObjectMapper
) {
    protected abstract fun toOAuthUser(attrs: Map<String, Any>): OAuthUser


    protected fun getSigninUrl(accessToken: String, state: String, user: OAuthUser): String {
        val token = UUID.randomUUID().toString()
        return SecurityConfiguration.OAUTH_SIGNIN_PATTERN +
                "?" + SecurityConfiguration.PARAM_ACCESS_TOKEN + "=$token" +
                "&" + SecurityConfiguration.PARAM_STATE + "=$state" +
                "&" + SecurityConfiguration.PARAM_USER + "=" + URLEncoder.encode(objectMapper.writeValueAsString(user), "utf-8")
    }

    protected fun generateState(request: HttpServletRequest): String {
        val state = UUID.randomUUID().toString()
        request.session.setAttribute(SecurityConfiguration.SESSION_STATE, state)
        return state
    }
}
