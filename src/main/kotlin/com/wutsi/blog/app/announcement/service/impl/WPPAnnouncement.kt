package com.wutsi.blog.app.announcement.service.impl

import com.wutsi.blog.app.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.partner.service.PartnerService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Service

@Service
class WPPAnnouncement(
        private val requestContext: RequestContext,
        private val partners: PartnerService
): Announcement {
    override fun supports(pageName: String) = PageName.BLOG.equals(pageName)
            || PageName.HOME.equals(pageName)

    override fun show(): Boolean {
        val user = requestContext.currentUser()
        val toggles = requestContext.toggles()
        return toggles.wpp && user?.blog == true && !partners.isPartner()
    }

    override fun name() = "wpp"

    override fun actionUrl(): String = "/partner"
}
