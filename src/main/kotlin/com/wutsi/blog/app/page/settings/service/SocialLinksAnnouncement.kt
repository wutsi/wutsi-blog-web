package com.wutsi.blog.app.page.settings.service

import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.announcement.service.impl.AbstractAnnouncement
import org.springframework.stereotype.Service

@Service
class SocialLinksAnnouncement(
        requestContext: RequestContext
): AbstractAnnouncement(requestContext) {
    override fun show(page: String): Boolean {
        val user = requestContext.currentUser()
        return user?.blog == true
                && user?.hasSocialLinks == false
                && user.loginCount < Announcement.MAX_LOGIN
    }

    override fun name() = "social_links"

    override fun actionUrl() = "/me/settings"
}
