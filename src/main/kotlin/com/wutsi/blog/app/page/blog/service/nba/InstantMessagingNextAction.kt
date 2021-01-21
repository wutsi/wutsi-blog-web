package com.wutsi.blog.app.page.blog.service.nba

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.NextActionModel
import com.wutsi.blog.app.page.blog.service.NextAction
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.settings.model.UserModel

class InstantMessagingNextAction(private val requestContext: RequestContext) : NextAction {
    override fun get(blog: UserModel, channels: List<ChannelModel>): NextActionModel? {
        if (!blog.hasInstantMessagingLinks) {
            return NextActionModel(
                name = "instant_messaging",
                title = requestContext.getMessage("next-action.item.instant_messaging"),
                iconUrl = "/assets/wutsi/img/social/whatsapp.png",
                url = "/me/settings?highlight=instant_messaging-container#instant_messaging"
            )
        }
        return null
    }
}
