package com.wutsi.blog.app.component.comment.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.announcement.service.impl.AbstractAnnouncement
import org.springframework.stereotype.Service


@Service
@Deprecated("unused")
class CommentAnnouncement(
        requestContext: RequestContext
): AbstractAnnouncement(requestContext) {
    override fun show(): Boolean {
        return requestContext.toggles().comment
    }

    override fun name() = "comment"

    override fun actionUrl() = null
}
