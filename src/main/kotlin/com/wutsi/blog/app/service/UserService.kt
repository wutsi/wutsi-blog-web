package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.UserAttributeForm
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.UpdateUserAttributeRequest
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class UserService(
        private val backend: UserBackend,
        private val mapper: UserMapper,
        private val requestContext: RequestContext,
        private val request: HttpServletRequest,
        private val response: HttpServletResponse

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

    fun canReadStoriesInAllLanguages(): Boolean? {
        val user = requestContext.currentUser()
        val readAll: Boolean?
        if (user != null){
            return user.readAllLanguages
        } else {
            return CookieHelper.get(CookieName.READ_ALL_LANGUAGE, request)?.toBoolean()
        }
    }

    fun setReadStoriesInAllLanguages(value: Boolean) {
        val user = requestContext.currentUser()
        if (user != null){
            set(UserAttributeForm(
                    name = "read_all_languages",
                    value = value.toString()
            ))
        }

        /* update cookie */
        CookieHelper.put(CookieName.READ_ALL_LANGUAGE, value.toString(), request, response)
    }
}

