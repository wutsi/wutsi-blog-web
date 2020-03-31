package com.wutsi.blog.app.security

import com.wutsi.blog.app.backend.AuthenticationBackend
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class LogoutHandlerImpl(private val backend: AuthenticationBackend): LogoutHandler {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LogoutHandlerImpl::class.java)
    }

    override fun logout(request: HttpServletRequest, response: HttpServletResponse, auth: Authentication) {
        val accessToken = auth.name
        try {
            backend.logout(accessToken)
        } catch(ex: Exception) {
            LOGGER.warn("Logout failed", ex)
        }
    }
}
