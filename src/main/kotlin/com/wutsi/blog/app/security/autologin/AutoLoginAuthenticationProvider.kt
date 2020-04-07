package com.wutsi.blog.app.security.autologin

import com.wutsi.blog.app.backend.AuthenticationBackend
import org.apache.http.auth.BasicUserPrincipal
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority


class AutoLoginAuthenticationProvider(
        private val backend: AuthenticationBackend
) : AuthenticationProvider {
    override fun authenticate(auth: Authentication): Authentication {
        try {
            val authentication = auth as AutoLoginAuthentication
            authenticate(authentication)
            return auth
        } catch (ex: Exception) {
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
