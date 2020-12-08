package com.wutsi.blog.app.page.channel.service

import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.client.channel.ChannelDto
import com.wutsi.blog.client.channel.ChannelType
import org.springframework.stereotype.Service

@Service
class ChannelMapper {
    fun toChannelModel(obj: ChannelDto) = ChannelModel(
        id = obj.id,
        name = obj.name,
        type = obj.type,
        userId = obj.userId,
        pictureUrl = obj.pictureUrl,
        connected = true
    )

    fun toChannelModel(type: ChannelType) = ChannelModel(
        type = type,
        connected = false
    )
}
