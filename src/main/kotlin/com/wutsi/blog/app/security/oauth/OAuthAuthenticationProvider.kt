package com.wutsi.blog.app.security.oauth

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.security.config.SecurityConfiguration
import com.wutsi.blog.client.user.AuthenticateRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest


@Component
class OAuthAuthenticationProvider(
        private val backend: AuthenticationBackend,
        private val request: HttpServletRequest
) : AuthenticationProvider {
    override fun authenticate(auth: Authentication): Authentication {
        try {
            validateState()
            val authentication = auth as OAuthTokenAuthentication
            return authenticate(authentication)
        } finally {
            clearState()
        }
    }

    private fun authenticate(authentication: OAuthTokenAuthentication): Authentication {
        backend.login(AuthenticateRequest(
                accessToken = authentication.accessToken,
                provider = authentication.principal.user.provider,
                pictureUrl = user.pictureUrl,
                fullName = user.fullName,
                email = user.email,
                providerUserId = user.id,
                language = LocaleContextHolder.getLocale().language
        ))

        authentication.setAuthenticated(true)
        request.getSession(true).maxInactiveInterval = 84600    // 1d
        return authentication
    }

    override fun supports(clazz: Class<*>) = OAuthTokenAuthentication::class.java == clazz

    private fun validateState() {
        val state = request.getParameter("state")
        if (state == null || state.isEmpty() || state != request.session.getAttribute(SecurityConfiguration.SESSION_STATE)){
            throw BadCredentialsException("invalid_state")
        }
    }

    private fun clearState() {
        request.session.removeAttribute(SecurityConfiguration.SESSION_STATE)
    }
}
