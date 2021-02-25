package com.wutsi.blog.app.common.service

import com.wutsi.blog.app.backend.ViewBackend
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.view.SearchPreferredAuthorRequest
import com.wutsi.blog.client.view.SearchViewRequest
import org.apache.commons.lang.time.DateUtils
import org.springframework.stereotype.Service
import java.util.Date

@Service
class ViewService(
    private val viewBackend: ViewBackend,
    private val requestContext: RequestContext,
    private val userService: UserService
) {
    fun findViewedStoryIdsLastWeek(): Collection<Long> {
        val now = Date()
        val views = viewBackend.search(
            SearchViewRequest(
                userId = requestContext.currentUser()?.id,
                deviceId = requestContext.deviceId(),
                viewStartDate = DateUtils.addDays(now, -7),
                viewEndDate = now
            )
        ).views
        return views.map { it.storyId }.toSet()
    }

    fun findPreferredAuthors(limit: Int = 3): List<UserModel> {
        val authorIds = viewBackend.preferedAuthors(
            SearchPreferredAuthorRequest(
                userId = requestContext.currentUser()?.id,
                deviceId = requestContext.deviceId()
            )
        ).authors.map { it.authorId }.take(limit)
        if (authorIds.isEmpty()) {
            return emptyList()
        }

        return userService.search(
            SearchUserRequest(
                userIds = authorIds,
                limit = authorIds.size
            )
        )
    }
}
