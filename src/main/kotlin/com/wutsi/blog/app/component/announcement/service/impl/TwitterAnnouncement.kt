package com.wutsi.blog.app.component.announcement.service.impl

import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.client.channel.ChannelType
import org.springframework.stereotype.Service


@Service
class TwitterAnnouncement(
        private val requestContext: RequestContext,
        private val channels: ChannelService
): Announcement {
    override fun show(): Boolean {
        val toggles = requestContext.toggles()
        if (toggles.channel && toggles.channelTwitter && requestContext.currentUser()?.blog == true){
            val twitter = channels.all().find { it.connected && it.type == ChannelType.twitter }
            return twitter == null
        } else {
            return false
        }
    }

    override fun name() = "twitter"

    override fun actionUrl() = "/me/channel"
}
