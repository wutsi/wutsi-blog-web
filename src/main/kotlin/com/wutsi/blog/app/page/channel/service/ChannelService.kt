package com.wutsi.blog.app.page.channel.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.Toggles
import com.wutsi.blog.app.page.channel.model.ChannelModel
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.CreateChannelRequest
import com.wutsi.blog.sdk.ChannelApi
import org.springframework.stereotype.Service

@Service
class ChannelService(
    private val api: ChannelApi,
    private val mapper: ChannelMapper,
    private val requestContext: RequestContext,
    private val toggles: Toggles
) {
    fun all(): List<ChannelModel> {
        val user = requestContext.currentUser()
            ?: return emptyList()

        val channels = api.search(userId = user.id)
            .channels
            .map { it.type to it }.toMap()

        return channelTypes().map {
            val channel = channels[it]
            if (channel == null) mapper.toChannelModel(it) else mapper.toChannelModel(channel)
        }
    }

    fun get(id: Long): ChannelModel {
        val channel = api.get(id).channel
        return mapper.toChannelModel(channel)
    }

    fun create(
        id: String,
        accessToken: String,
        accessTokenSecret: String,
        name: String,
        pictureUrl: String,
        type: ChannelType
    ) {
        api.create(
            CreateChannelRequest(
                providerUserId = id,
                accessToken = accessToken,
                accessTokenSecret = accessTokenSecret,
                name = name,
                pictureUrl = pictureUrl,
                type = type,
                userId = requestContext.currentUser()?.id
            )
        )
    }

    fun delete(id: Long) {
        api.delete(id)
    }

    fun channelTypes(): List<ChannelType> {
        val types = mutableListOf<ChannelType>()
        if (toggles.channelTwitter) {
            types.add(ChannelType.twitter)
        }
        if (toggles.channelFacebook) {
            types.add(ChannelType.facebook)
        }
        if (toggles.channelLinkedin) {
            types.add(ChannelType.linkedin)
        }
        return types
    }
}
