package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.client.user.SearchUserRequest
import org.springframework.stereotype.Service

@Service
class UserService(
        private val backend: UserBackend,
        private val mapper: UserMapper
) {
    fun get (id: Long): UserModel {
        val user = backend.get(id).user
        return mapper.toUserModel(user)
    }

    fun search(request: SearchUserRequest) : List<UserModel> {
        val users = backend.search(request).users
        return users.map { mapper.toUserModel(it) }
    }
}

