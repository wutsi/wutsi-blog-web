package com.wutsi.blog.app.page.calendar.model

import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.ChannelType.unknown
import com.wutsi.blog.client.post.PostStatus
import com.wutsi.blog.client.post.PostStatus.pending
import java.util.Date

data class CalendarPostModel(
    val id: Long = -1,
    val story: CalendarStoryModel = CalendarStoryModel(),
    val channelType: ChannelType = unknown,
    val channel: ChannelModel = ChannelModel(),
    val channelImageUrl: String = "",
    val status: PostStatus = pending,
    val message: String? = null,
    val pictureUrl: String? = null,
    val socialPostId: String? = null,
    val scheduledPostDateTime: Date = Date(),
    val postDateTime: Date? = null,
    val scheduledPostDateTimeText: String = "",
    val postDateTimeText: String? = null,
    val hasMessage: Boolean = false,
    val published: Boolean = false,
    val url: String = ""
)
