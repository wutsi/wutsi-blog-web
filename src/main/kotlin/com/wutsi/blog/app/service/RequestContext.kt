package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.UserModel
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component


@Component
@Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class RequestContext(
        private val mapper: UserMapper,
        private val backend: AuthenticationBackend,
        private val togglesHolder: TogglesHolder,
        private val oauth2ClientService: OAuth2AuthorizedClientService
) {
    var user: UserModel? = null

    fun currentUser(): UserModel? {
        if (user != null) {
            return user
        }

        val token = accessToken()
        if (token != null) {
            val response = backend.session(token)
            user = mapper.toUserModel(response.session.user)
        }
        return user
    }

    fun toggles() = togglesHolder.get()

    fun accessToken(): String? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is AnonymousAuthenticationToken) {
            return null
        }

        val ooauth2Token = auth as OAuth2AuthenticationToken
        val client = oauth2ClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(ooauth2Token.authorizedClientRegistrationId, auth.name)
        return client.accessToken.tokenValue
    }

}
