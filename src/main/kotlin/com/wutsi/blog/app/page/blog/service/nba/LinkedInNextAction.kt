package com.wutsi.blog.app.page.blog.service.nba

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.channel.ChannelType

class LinkedInNextAction(
    private val requestContext: RequestContext
) : AbstractChannelNextAction(requestContext) {
    override fun channelType() = ChannelType.linkedin

    override fun isEnabled() = requestContext.toggles().channelLinkedin
}
