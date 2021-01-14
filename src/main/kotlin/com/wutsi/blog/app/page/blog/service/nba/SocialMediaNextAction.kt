package com.wutsi.blog.app.page.blog.service.nba

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.NextActionModel
import com.wutsi.blog.app.page.blog.service.NextAction
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.settings.model.UserModel

class SocialMediaNextAction(private val requestContext: RequestContext) : NextAction {
    override fun get(blog: UserModel, channels: List<ChannelModel>): NextActionModel? {
        if (!blog.hasSocialLinks) {
            return NextActionModel(
                name = "social_media",
                title = requestContext.getMessage("next-action.item.social_media"),
                iconUrl = "/assets/wutsi/img/social_media.png",
                url = "/me/settings?highlight=social_media-container#social_media"
            )
        }
        return null
    }
}
