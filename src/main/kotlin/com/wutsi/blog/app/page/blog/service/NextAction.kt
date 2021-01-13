package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.page.blog.model.NextActionModel
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.app.page.settings.model.UserModel

interface NextAction {
    fun get(blog: UserModel, channel: List<ChannelModel>): NextActionModel?
}
