package com.wutsi.blog.app.component.announcement.service.impl

import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.partner.service.PartnerService
import com.wutsi.blog.app.page.payment.service.ContractService
import org.springframework.stereotype.Service

@Service
class WPPAnnouncement(
        private val requestContext: RequestContext,
        private val partners: PartnerService,
        private val contracts: ContractService
): Announcement {
    override fun show(): Boolean {
        val user = requestContext.currentUser()
        val toggles = requestContext.toggles()
        return toggles.wpp && user?.blog == true && !partners.isPartner() && !contracts.hasContract()
    }

    override fun name() = "wpp"

    override fun actionUrl() = "/partner"
}
