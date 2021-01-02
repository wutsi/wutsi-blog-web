package com.wutsi.blog.app.page.calendar

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.calendar.model.CalendarPostModel
import com.wutsi.blog.app.page.calendar.service.PostService
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.core.util.DateUtils

abstract class AbstractPostController(
    protected val postService: PostService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    protected abstract fun requiredPermissions(): List<Permission>

    private fun checkAccess(post: CalendarPostModel) {
        requestContext.checkAccess(post.story.innerStory, requiredPermissions())
    }

    protected fun getPost(id: Long): CalendarPostModel {
        val post = postService.get(id)
        checkAccess(post)
        return post
    }

    protected fun calendarUrl(post: CalendarPostModel): String =
        "/me/calendar?date=" + DateUtils.beginningOfTheWeek(
            DateUtils.toLocalDate(post.scheduledPostDateTime)
        )

    protected fun postUrl(id: Long): String =
        "/me/calendar/post?id=$id"
}
