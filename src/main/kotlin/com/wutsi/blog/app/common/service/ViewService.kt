package com.wutsi.blog.app.common.service

import com.wutsi.blog.app.backend.ViewBackend
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.view.SearchPreferredAuthorRequest
import org.springframework.stereotype.Service


@Service
class ViewService(
        private val viewBackend: ViewBackend,
        private val requestContext: RequestContext,
        private val userService: UserService
) {
    fun findPreferredAuthors(user: UserModel?, limit: Int = 3): List<UserModel> {
        val authorIds = viewBackend.preferedAuthors(SearchPreferredAuthorRequest(
                userId = user?.id,
                deviceId = requestContext.deviceId()
        )).authors.map { it.authorId }.take(limit)
        if (authorIds.isEmpty()) {
            return emptyList()
        }

        return userService.search(SearchUserRequest(
                userIds = authorIds,
                limit = authorIds.size
        ))
    }

}
