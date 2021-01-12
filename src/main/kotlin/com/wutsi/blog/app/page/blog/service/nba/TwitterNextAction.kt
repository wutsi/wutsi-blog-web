package com.wutsi.blog.app.page.blog.service.nba

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.NextBestActionModel
import com.wutsi.blog.app.page.blog.service.NextAction
import com.wutsi.blog.app.page.settings.model.UserModel

class TwitterNextAction(private val requestContext: RequestContext) : NextAction {
    override fun get(blog: UserModel): NextBestActionModel? {
        if (blog.twitterId.isNullOrEmpty()) {
            return NextBestActionModel(
                name = "twitter",
                title = requestContext.getMessage("next-action.item.twitter"),
                iconUrl = "/assets/wutsi/img/social/twitter.png",
                url = "/me/settings?highlight=channels-container#channels"
            )
        }
        return null
    }
}
