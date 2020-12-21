package com.wutsi.blog.app.page.calendar.model

import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.ChannelType.unknown

data class CreatePostForm(
    val storyId: String = "",
    val channelType: ChannelType = unknown,
    val message: String = "",
    val scheduledDateTime: String = ""
)
