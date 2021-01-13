package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.page.blog.model.NextActionModel
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.settings.model.UserModel

class NextActionSet(
    private val actions: List<NextAction>
) : NextAction {
    override fun get(blog: UserModel, channels: List<ChannelModel>): NextActionModel? {
        val result = mutableListOf<NextActionModel>()
        actions.forEach {
            val action = it.get(blog, channels)
            if (action != null) {
                result.add(action)
            }
        }
        return if (result.isEmpty()) null else result.random()
    }
}
