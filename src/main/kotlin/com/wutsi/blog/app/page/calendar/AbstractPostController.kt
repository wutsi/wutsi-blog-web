package com.wutsi.blog.app.page.calendar

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.calendar.model.CalendarPostModel
import com.wutsi.blog.app.page.calendar.service.PostService
import com.wutsi.blog.app.security.model.Permission

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
}
