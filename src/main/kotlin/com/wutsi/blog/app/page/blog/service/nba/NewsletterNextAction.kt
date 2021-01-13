package com.wutsi.blog.app.page.blog.service.nba

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.NextActionModel
import com.wutsi.blog.app.page.blog.service.NextAction
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.settings.model.UserModel

class NewsletterNextAction(private val requestContext: RequestContext) : NextAction {
    override fun get(blog: UserModel, channels: List<ChannelModel>): NextActionModel? {
        if (blog.newsletterDeliveryDayOfWeek <= 0) {
            return NextActionModel(
                name = "newsletter",
                title = requestContext.getMessage("next-action.item.newsletter"),
                iconUrl = "/assets/wutsi/img/newsletter.png",
                url = "/me/settings?highlight=newsletter-container#newsletter"
            )
        }
        return null
    }
}
