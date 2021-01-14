package com.wutsi.blog.app.page.blog.service.nba

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.NextActionModel
import com.wutsi.blog.app.page.blog.service.NextAction
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.settings.model.UserModel

class WebsiteNextAction(private val requestContext: RequestContext) : NextAction {
    override fun get(blog: UserModel, channels: List<ChannelModel>): NextActionModel? {
        if (blog.websiteUrl.isNullOrEmpty()) {
            return NextActionModel(
                name = "website",
                title = requestContext.getMessage("next-action.item.website"),
                iconUrl = "/assets/wutsi/img/website.png",
                url = "/me/settings?highlight=website-container#website"
            )
        }
        return null
    }
}
