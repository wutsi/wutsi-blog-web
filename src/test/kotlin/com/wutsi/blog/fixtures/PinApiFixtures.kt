package com.wutsi.blog.fixtures

import com.wutsi.blog.client.pin.GetPinResponse
import com.wutsi.blog.client.pin.PinDto

object PinApiFixtures {
    fun createPinDto(userId:Long, storyId:Long) = PinDto(
            id = userId,
            userId = userId,
            storyId = storyId
    )

    fun createGetPinResponse(userId:Long, storyId:Long) = createGetPinResponse(createPinDto(userId, storyId))

    fun createGetPinResponse(pin: PinDto) = GetPinResponse(pin = pin)
}
