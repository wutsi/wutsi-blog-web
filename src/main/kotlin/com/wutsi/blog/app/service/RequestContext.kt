package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.core.exception.ForbiddenException
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest


@Component
@Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class RequestContext(
        private val mapper: UserMapper,
        private val authBackend: AuthenticationBackend,
        private val userBackend: UserBackend,
        private val togglesHolder: TogglesHolder,
        private val request: HttpServletRequest,
        private val tokenStorage: AccessTokenStorage,
        private val localization: LocalizationService,
        private val securityManager: SecurityManager
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RequestContext::class.java)
    }

    private var user: UserModel? = null

    fun currentUser(): UserModel? {
        if (user != null) {
            return user
        }

        val token = accessToken()
        if (token != null) {
            try {
                val response = authBackend.session(token)
                val usr = userBackend.get(response.session.userId).user
                user = mapper.toUserModel(usr)
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

    fun checkAccess(story: StoryModel, requiredPermissions: List<Permission>) {
        val permissions = securityManager.permissions(story, currentUser())
        if (!permissions.containsAll(requiredPermissions)){
            LOGGER.error("required-permissions=$requiredPermissions - permissions=$permissions")
            throw ForbiddenException("permission_error")
        }
    }
}
