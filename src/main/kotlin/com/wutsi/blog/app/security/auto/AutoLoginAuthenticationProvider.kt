package com.wutsi.blog.app.security.auto

import com.wutsi.blog.app.backend.AuthenticationBackend
import org.apache.http.auth.BasicUserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component


@Component
class AutoLoginAuthenticationProvider(
        private val backend: AuthenticationBackend
) : AuthenticationProvider {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AutoLoginAuthenticationProvider::class.java)
    }

    override fun authenticate(auth: Authentication): Authentication {
        try {
            val authentication = auth as AutoLoginAuthentication
            authenticate(authentication)
            return auth
        } catch (ex: Exception) {
            LOGGER.warn("Unable to authenticate $auth", ex)
            return createAnonymous()
        }
    }

    private fun authenticate(authentication: AutoLoginAuthentication) {
        backend.session(authentication.accessToken)
        authentication.setAuthenticated(true)
    }

    private fun createAnonymous() = AnonymousAuthenticationToken(
            "anomymous",
            BasicUserPrincipal("anomymous"),
            arrayListOf(SimpleGrantedAuthority("anomymous"))
    )

    override fun supports(clazz: Class<*>) = AutoLoginAuthentication::class.java == clazz
}
