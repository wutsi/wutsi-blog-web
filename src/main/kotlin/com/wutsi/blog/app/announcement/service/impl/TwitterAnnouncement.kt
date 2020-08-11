package com.wutsi.blog.app.announcement.service.impl

import com.wutsi.blog.app.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.client.channel.ChannelType
import org.springframework.stereotype.Service


@Service
class TwitterAnnouncement(
        private val requestContext: RequestContext,
        private val channels: ChannelService
): Announcement {
    override fun supports(pageName: String) = true

    override fun show(): Boolean {
        val twitter = channels.all().find { it.connected && it.type == ChannelType.twitter }
        val toggles = requestContext.toggles()
        return toggles.channel && toggles.channelTwitter &&
                requestContext.currentUser()?.blog == true &&
                twitter == null
    }

    override fun name() = "twitter"

    override fun actionUrl(): String = "/me/channel"
}
