package com.wutsi.blog.app.announcement.service.impl

import com.wutsi.blog.app.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import org.springframework.stereotype.Service


@Service
@Deprecated("unused")
class CommentAnnouncement(
        private val requestContext: RequestContext
): Announcement {
    override fun show(): Boolean {
        return requestContext.toggles().comment
    }

    override fun name() = "comment"

    override fun actionUrl() = null
}
