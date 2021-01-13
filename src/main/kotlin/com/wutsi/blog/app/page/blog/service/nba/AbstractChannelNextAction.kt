package com.wutsi.blog.app.page.blog.service.nba

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.NextActionModel
import com.wutsi.blog.app.page.blog.service.NextAction
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.client.channel.ChannelType

abstract class AbstractChannelNextAction(
    private val requestContext: RequestContext
) : NextAction {
    abstract fun channelType(): ChannelType

    abstract fun isEnabled(): Boolean

    override fun get(blog: UserModel, channels: List<ChannelModel>): NextActionModel? {
        if (!isEnabled())
            return null

        if (!isConnected(channels)) {
            val type = channelType()
            return NextActionModel(
                name = type.name,
                title = requestContext.getMessage("next-action.item.$type"),
                iconUrl = "/assets/wutsi/img/social/$type.png",
                url = "/me/settings?highlight=channels-container#channels"
            )
        }
        return null
    }

    protected fun isConnected(channels: List<ChannelModel>): Boolean =
        channels.find { it.type == channelType() && it.connected } != null
}
