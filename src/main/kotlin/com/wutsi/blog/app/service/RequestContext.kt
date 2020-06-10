package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.core.exception.ForbiddenException
import com.wutsi.core.exception.NotFoundException
import com.wutsi.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


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
        private val securityManager: SecurityManager,
        private val logger: KVLogger,
        private val response: HttpServletResponse
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
                if (e is NotFoundException){
                    tokenStorage.delete(response)
                }
                return null
            }
        }
        return user
    }

    fun toggles() = togglesHolder.get()

    fun accessToken(): String? {
        return tokenStorage.get(request)
    }

    fun language(): String {
        val language = currentUser()?.language
        return if (language == null) LocaleContextHolder.getLocale().language else language
    }

    fun getMessage(key: String, defaultKey: String? = null): String {
        try {
            return localization.getMessage(key)
        } catch (ex: Exception){
            if (defaultKey != null){
                try {
                    return localization.getMessage(defaultKey)
                } catch(ex2: Exception){
                }
            }
            return key
        }
    }

    fun checkAccess(story: StoryModel, requiredPermissions: List<Permission>) {
        val permissions = securityManager.permissions(story, currentUser())

        logger.add("PermissionsUser", permissions)
        logger.add("PermissionsExpected", requiredPermissions)
        if (!permissions.containsAll(requiredPermissions)){
            LOGGER.error("required-permissions=$requiredPermissions - permissions=$permissions")
            throw ForbiddenException("permission_error")
        }
    }
}
