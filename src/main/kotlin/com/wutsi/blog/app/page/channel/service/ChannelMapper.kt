package com.wutsi.blog.app.page.channel.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.client.channel.ChannelDto
import com.wutsi.blog.client.channel.ChannelType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ChannelMapper(
    private val requestContext: RequestContext,
    @Value("\${wutsi.asset-url}") private val assetUrl: String
) {
    fun toChannelModel(obj: ChannelDto) = ChannelModel(
        id = obj.id,
        name = requestContext.getMessage("channel.${obj.type}"),
        type = obj.type,
        userId = obj.userId,
        pictureUrl = "$assetUrl/assets/wutsi/img/social/${obj.type}.png",
        connected = true
    )

    fun toChannelModel(type: ChannelType) = ChannelModel(
        type = type,
        name = requestContext.getMessage("channel.$type"),
        connected = false,
        pictureUrl = "$assetUrl/assets/wutsi/img/social/$type.png"
    )
}
