package com.wutsi.blog.app.page.calendar.model

import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.ChannelType.unknown
import com.wutsi.blog.client.post.PostStatus
import com.wutsi.blog.client.post.PostStatus.pending
import java.util.Date

data class CalendarPostModel(
    val id: Long = -1,
    val story: CalendarStoryModel = CalendarStoryModel(),
    val channelType: ChannelType = unknown,
    val channelImageUrl: String = "",
    val status: PostStatus = pending,
    val message: String? = null,
    val pictureUrl: String? = null,
    val socialPostId: String? = null,
    val scheduledPostDateTime: Date,
    val postDateTime: Date? = null
)
