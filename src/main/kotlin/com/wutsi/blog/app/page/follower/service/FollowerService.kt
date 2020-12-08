package com.wutsi.blog.app.page.follower.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.follower.CreateFollowerRequest
import com.wutsi.blog.client.follower.SearchFollowerRequest
import com.wutsi.blog.sdk.FollowerApi
import org.springframework.stereotype.Service

@Service
class FollowerService(
    private val api: FollowerApi,
    private val requestContext: RequestContext
) {
    fun follow(userId: Long): Long = api.create(
        CreateFollowerRequest(
            userId = userId,
            followerUserId = requestContext.currentUser()?.id
        )
    ).followerId

    fun unfollow(userId: Long) {
        val followers = api.search(
            SearchFollowerRequest(
                userId = userId,
                followerUserId = requestContext.currentUser()?.id
            )
        ).followers
        if (followers.isNotEmpty()) {
            api.delete(followers[0].id)
        }
    }

    fun searchFollowingUserIds(): List<Long> {
        if (!requestContext.toggles().follow || requestContext.currentUser() == null)
            return emptyList()

        return api.search(
            SearchFollowerRequest(
                followerUserId = requestContext.currentUser()?.id
            )
        ).followers.map { it.userId }
    }

    fun canFollow(id: Long): Boolean {
        if (!requestContext.toggles().follow || requestContext.currentUser()?.id == id)
            return false

        if (requestContext.currentUser() == null)
            return true

        return api.search(
            SearchFollowerRequest(
                userId = id,
                followerUserId = requestContext.currentUser()?.id
            )
        ).followers.isEmpty()
    }
}
