package com.wutsi.blog.app.page.blog.service.nba

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.NextBestActionModel
import com.wutsi.blog.app.page.blog.service.NextAction
import com.wutsi.blog.app.page.settings.model.UserModel

class LinkedInNextAction(private val requestContext: RequestContext) : NextAction {
    override fun get(blog: UserModel): NextBestActionModel? {
        if (blog.linkedinId.isNullOrEmpty()) {
            return NextBestActionModel(
                name = "linkedin",
                title = requestContext.getMessage("next-action.item.linkedin"),
                iconUrl = "/assets/wutsi/img/social/linkedin.png",
                url = "/me/settings?highlight=channels-container#channels"
            )
        }
        return null
    }
}
