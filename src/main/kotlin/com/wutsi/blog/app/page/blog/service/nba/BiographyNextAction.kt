package com.wutsi.blog.app.page.blog.service.nba

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.NextBestActionModel
import com.wutsi.blog.app.page.blog.service.NextAction
import com.wutsi.blog.app.page.settings.model.UserModel

class BiographyNextAction(private val requestContext: RequestContext) : NextAction {
    override fun get(blog: UserModel): NextBestActionModel? {
        if (blog.biography.isNullOrEmpty()) {
            return NextBestActionModel(
                name = "biography",
                title = requestContext.getMessage("next-action.item.biography"),
                iconUrl = "/assets/wutsi/img/settings.png",
                url = "/me/settings?highlight=biography-container#general"
            )
        }
        return null
    }
}
