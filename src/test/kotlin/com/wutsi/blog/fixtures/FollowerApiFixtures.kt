package com.wutsi.blog.fixtures

import com.wutsi.blog.client.follower.FollowerDto

object FollowerApiFixtures {
    fun createFolloweDto(
        userId: Long,
        followerUserId: Long
    ) = FollowerDto(
        id = System.currentTimeMillis(),
        userId = userId,
        followerUserId = followerUserId
    )
}
