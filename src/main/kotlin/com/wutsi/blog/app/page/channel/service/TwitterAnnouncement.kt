package com.wutsi.blog.app.page.channel.service

import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.announcement.service.impl.AbstractAnnouncement
import com.wutsi.blog.client.channel.ChannelType
import org.springframework.stereotype.Service


@Service
class TwitterAnnouncement(
        requestContext: RequestContext,
        private val channels: ChannelService
): AbstractAnnouncement(requestContext) {
    override fun show(): Boolean {
        val toggles = requestContext.toggles()
        val user = requestContext.currentUser()
        if (toggles.channel && toggles.channelTwitter && user?.blog == true && user.loginCount < Announcement.MAX_LOGIN){
            val twitter = channels.all().find { it.connected && it.type == ChannelType.twitter }
            return twitter == null
        } else {
            return false
        }
    }

    override fun name() = "twitter"

    override fun actionUrl() = "/me/channel"
}
