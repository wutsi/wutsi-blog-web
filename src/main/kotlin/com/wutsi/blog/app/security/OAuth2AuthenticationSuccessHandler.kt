package com.wutsi.blog.app.security

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.client.user.AuthenticateRequest
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse




@Component
class OAuth2AuthenticationSuccessHandler(
        private val authenticationBackend: AuthenticationBackend,
        private val mapper: UserMapper
) : SimpleUrlAuthenticationSuccessHandler() {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        super.onAuthenticationSuccess(request, response, authentication)

        login(authentication)
    }

    private fun login(auth: Authentication) {
        val user = mapper.toUserModel(auth)
        authenticationBackend.login(AuthenticateRequest(
                provider = user.provider,
                pictureUrl = user.pictureUrl,
                fullName = user.fullName,
                email = user.email,
                providerUserId = user.id
        ))
    }
}
