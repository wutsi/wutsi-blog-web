
package com.wutsi.blog.app.component.announcement.service.impl

import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import org.springframework.stereotype.Service


abstract class AbstractAnnouncement(
        protected val requestContext: RequestContext
): Announcement {
    override fun description(): String {
        val name = name()
        return requestContext.getMessage("announcement.${name}.message")
    }
}
