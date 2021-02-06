package com.wutsi.blog.fixtures

import com.wutsi.blog.client.pin.GetPinResponse
import com.wutsi.blog.client.pin.PinDto
import java.util.Date

object PinApiFixtures {
    fun createPinDto(userId: Long, storyId: Long, creationDateTime: Date = Date()) = PinDto(
        id = userId,
        userId = userId,
        storyId = storyId,
        creationDateTime = creationDateTime
    )

    fun createGetPinResponse(userId: Long, storyId: Long, creationDateTime: Date = Date()) =
        createGetPinResponse(createPinDto(userId, storyId, creationDateTime))

    fun createGetPinResponse(pin: PinDto) = GetPinResponse(pin = pin)
}
