package com.wutsi.blog.app.page.blog.service.nba

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.channel.ChannelType.twitter

class TwitterNextAction(
    private val requestContext: RequestContext
) : AbstractChannelNextAction(requestContext) {
    override fun isEnabled() = requestContext.toggles().channelTwitter

    override fun channelType() = twitter
}
