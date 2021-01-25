package com.wutsi.blog.app.common.service

import com.wutsi.blog.app.backend.ViewBackend
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.view.SearchPreferredAuthorRequest
import com.wutsi.blog.client.view.SearchViewRequest
import com.wutsi.core.util.DateUtils
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ViewService(
    private val viewBackend: ViewBackend,
    private val requestContext: RequestContext,
    private val userService: UserService
) {
    fun storiesViewedThisMonth(): Set<Long> {
        val today = LocalDate.now()
        val startDate = DateUtils.toDate(today.year, today.monthValue, 1)
        val request = SearchViewRequest(
            deviceId = requestContext.deviceId(),
            userId = requestContext.currentUser()?.id,
            viewStartDate = DateUtils.beginingOfTheDay(startDate),
            viewEndDate = DateUtils.endOfTheDay(
                DateUtils.addDays(
                    DateUtils.addMonths(startDate, 1),
                    -1
                )
            ),
            limit = 100
        )
        val views = viewBackend.search(request).views
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
