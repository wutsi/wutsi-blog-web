package com.wutsi.blog.app.security.auto

import com.wutsi.blog.app.backend.AuthenticationBackend
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component


@Component
class AutoLoginAuthenticationProvider(
        private val backend: AuthenticationBackend
) : AuthenticationProvider {
    override fun authenticate(auth: Authentication): Authentication {
        val authentication = auth as AutoLoginAuthentication
        authenticate(authentication)
        return auth
    }

    private fun authenticate(authentication: AutoLoginAuthentication) {
        backend.session(authentication.accessToken)
        authentication.setAuthenticated(true)
    }

    override fun supports(clazz: Class<*>) = AutoLoginAuthentication::class.java == clazz
}
