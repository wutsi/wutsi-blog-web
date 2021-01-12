package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.page.blog.model.NextBestActionModel
import com.wutsi.blog.app.page.settings.model.UserModel

class NextActionSet(
    private val actions: List<NextAction>
) : NextAction {
    override fun get(blog: UserModel): NextBestActionModel? {
        actions.forEach {
            val action = it.get(blog)
            if (action != null) {
                return action
            }
        }
        return null
    }
}
