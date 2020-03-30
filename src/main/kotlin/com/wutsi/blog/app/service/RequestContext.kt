package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.security.AccessTokenStorage
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
        private val request: HttpServletRequest,
        private val tokenStorage: AccessTokenStorage,
        private val localization: LocalizationService
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
        return tokenStorage.get(request)
    }

    fun getMessage(key: String) = localization.getMessage(key)

}
