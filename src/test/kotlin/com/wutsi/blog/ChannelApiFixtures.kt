package com.wutsi.blog

import com.wutsi.blog.client.channel.ChannelDto
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.pin.GetPinResponse
import com.wutsi.blog.client.pin.PinDto
import java.util.UUID

object ChannelApiFixtures {
    fun createChannelDto(
            userId:Long,
            type: ChannelType,
            pictureUrl: String = "https://avatars3.githubusercontent.com/u/39621277?v=4"
    ) = ChannelDto(
            id = System.currentTimeMillis(),
            userId = userId,
            type = type,
            pictureUrl = pictureUrl,
            name = "Name $userId",
            providerUserId = UUID.randomUUID().toString()
    )
}
