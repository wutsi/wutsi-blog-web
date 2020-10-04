package com.wutsi.blog.app.component.announcement.service.impl

import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import org.springframework.stereotype.Service


@Service
class SocialLinksAnnouncement(
        private val requestContext: RequestContext
): Announcement {
    override fun show(): Boolean {
        val user = requestContext.currentUser()
        return user?.hasSocialLinks == false && user.loginCount < Announcement.MAX_LOGIN
    }

    override fun name() = "social_links"

    override fun actionUrl() = "/me/settings"
}
