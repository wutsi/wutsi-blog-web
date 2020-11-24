package com.wutsi.blog.app.page.channel.service

import com.wutsi.blog.app.component.announcement.service.Announcement
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.announcement.service.impl.AbstractAnnouncement
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.channel.ChannelType
import org.springframework.stereotype.Service


@Service
@Deprecated ("replaced by twitter NextAction in BlogController")
class TwitterAnnouncement(
        requestContext: RequestContext,
        private val channels: ChannelService
): AbstractAnnouncement(requestContext) {
    override fun show(page: String): Boolean {
        val toggles = requestContext.toggles()
        val user = requestContext.currentUser()
        if (
                toggles.channel
                && toggles.channelTwitter
                && user?.blog == true
                && PageName.BLOG.equals(page)
        ){
            val twitter = channels.all().find { it.connected && it.type == ChannelType.twitter }
            return twitter == null
        } else {
            return false
        }
    }

    override fun name() = "twitter"

    override fun actionUrl() = "/me/channel"

    override fun iconUrl() = "/assets/wutsi/img/social/twitter.png"

    override fun cookieMaxAge(): Int = CookieHelper.ONE_DAY_SECONDS

    override fun autoHide(): Boolean = false
}
