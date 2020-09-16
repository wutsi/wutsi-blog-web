package com.wutsi.blog.app.component.announcement.service.impl

import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import org.springframework.stereotype.Service


@Service
class SocialLinksAnnouncement(
        private val requestContext: RequestContext
): Announcement {
    override fun show(): Boolean {
        return requestContext.currentUser()?.hasSocialLinks == false
    }

    override fun name() = "social_links"

    override fun actionUrl() = "/me/settings"
}
