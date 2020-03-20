package com.wutsi.blog.app.service

import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.UserModel
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component


@Component
@Scope(value="request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class RequestContext(
        private val mapper: UserMapper,
        val togglesHolder: TogglesHolder
) {

    fun currentUser(): UserModel? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is AnonymousAuthenticationToken) {
            return null
        } else {
            return mapper.toUserModel(auth)
        }
    }

    fun toggles() = togglesHolder.get()
}
