package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.security.AccessTokenCookieFilter
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest


@Component
@Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class RequestContext(
        private val mapper: UserMapper,
        private val backend: AuthenticationBackend,
        private val togglesHolder: TogglesHolder,
//        private val oauth2ClientService: OAuth2AuthorizedClientService,
        private val request: HttpServletRequest
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RequestContext::class.java)
    }

    var user: UserModel? = null

    fun currentUser(): UserModel? {
        if (user != null) {
            return user
        }

        val token = accessToken()
        if (token != null) {
            try {
                val response = backend.session(token)
                user = mapper.toUserModel(response.session.user)
            } catch (e: Exception){
                LOGGER.warn("Unable to resolve user associate with access_token ${token}", e)
                return null
            }
        }
        return user
    }

    fun toggles() = togglesHolder.get()

    fun accessToken(): String? {
        val cookie = AccessTokenCookieFilter.getCookie(request)
        return if (cookie == null) null else cookie.value
//        val auth = SecurityContextHolder.getContext().authentication
//        if (auth is AnonymousAuthenticationToken) {
//            return null
//        }
//
//        val ooauth2Token = auth as OAuth2AuthenticationToken
//        val client = oauth2ClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(ooauth2Token.authorizedClientRegistrationId, auth.name)
//        return client.accessToken.tokenValue
    }

}
