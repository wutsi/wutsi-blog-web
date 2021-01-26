package com.wutsi.blog.fixtures

import com.wutsi.blog.client.follower.CreateFollowerResponse
import com.wutsi.blog.client.follower.FollowerDto
import com.wutsi.blog.client.follower.SearchFollowerResponse

object FollowerApiFixtures {
    fun createFolloweDto(userId: Long, followerUserId: Long) = FollowerDto(
        id = System.currentTimeMillis(),
        userId = userId,
        followerUserId = followerUserId
    )

    fun createSearchFollowerResponse(userId: Long, followerUserId: Long) = SearchFollowerResponse(
        followers = listOf(createFolloweDto(userId, followerUserId))
    )

    fun createSearchFollowerResponse(followerId: Long) = CreateFollowerResponse(
        followerId = followerId
    )
}
