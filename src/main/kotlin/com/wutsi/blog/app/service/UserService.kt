package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.UserAttributeForm
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.UpdateUserAttributeRequest
import org.springframework.stereotype.Service

@Service
class UserService(
        private val backend: UserBackend,
        private val mapper: UserMapper,
        private val requestContext: RequestContext
) {
    fun get (id: Long): UserModel {
        val user = backend.get(id).user
        return mapper.toUserModel(user)
    }

    fun get(name: String): UserModel {
        val user = backend.get(name).user
        return mapper.toUserModel(user)
    }

    fun search(request: SearchUserRequest) : List<UserModel> {
        val users = backend.search(request).users
        return users.map { mapper.toUserModel(it) }
    }

    fun set(request: UserAttributeForm) {
        backend.update(
                requestContext.currentUser()?.id!!,
                UpdateUserAttributeRequest(
                        name = request.name,
                        value = request.value.trim()
                )
        )
    }
}

