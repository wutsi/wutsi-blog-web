package com.wutsi.blog.fixtures

import com.wutsi.blog.client.like.LikeCountDto
import com.wutsi.blog.client.like.LikeDto
import java.util.Date

object LikeApiFixtures {
    fun createLikeDto(userId: Long, storyId: Long) = LikeDto(
        id = System.currentTimeMillis(),
        userId = userId,
        storyId = storyId,
        likeDateTime = Date()
    )

    fun createLikeCountDto(storyId: Long, value: Long) = LikeCountDto(
        storyId = storyId,
        value = value
    )
}
