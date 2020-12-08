
package com.wutsi.blog.app.component.announcement.service.impl

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.announcement.service.Announcement

abstract class AbstractAnnouncement(
    protected val requestContext: RequestContext
) : Announcement {
    override fun title(): String {
        val name = name()
        return requestContext.getMessage("announcement.$name.title", "label.notification")
    }

    override fun actionText(): String? {
        val name = name()
        return requestContext.getMessage("announcement.$name.action", "button.learn_more")
    }

    override fun description(): String {
        val name = name()
        return requestContext.getMessage("announcement.$name.message")
    }
}
