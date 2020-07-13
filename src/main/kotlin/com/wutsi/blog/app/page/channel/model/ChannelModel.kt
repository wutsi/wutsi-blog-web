package com.wutsi.blog.app.page.channel.model

import com.wutsi.blog.client.channel.ChannelType
import java.util.Date

data class ChannelModel(
        val id: Long = -1,
        val userId: Long? = null,
        val type: ChannelType = ChannelType.unknown,
        val name: String = "",
        val pictureUrl: String? = null,
        val connected: Boolean = false,
        val creationDateTime: Date = Date(),
        val modificationDateTime: Date = Date()
)
