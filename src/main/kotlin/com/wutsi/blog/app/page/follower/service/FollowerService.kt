package com.wutsi.blog.app.page.follower.service

import com.wutsi.blog.app.backend.FollowerBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.follower.CreateFollowerRequest
import com.wutsi.blog.client.follower.SearchFollowerRequest
import org.springframework.stereotype.Service

@Service
class FollowerService(
        private val backend: FollowerBackend,
        private val requestContext: RequestContext
) {
    fun follow(userId: Long): Long = backend.create(CreateFollowerRequest(
            userId = userId,
            followerUserId = requestContext.currentUser()?.id
        )).followerId


    fun searchFollowingUserIds(): List<Long> {
        if (!requestContext.toggles().follow || requestContext.currentUser() == null)
            return emptyList()

        return backend.search(SearchFollowerRequest(
                followerUserId = requestContext.currentUser()?.id
        )).followers.map { it.userId }
    }

    fun canFollow(id: Long): Boolean {
        if (!requestContext.toggles().follow || requestContext.currentUser()?.id == id)
            return false

        if (requestContext.currentUser() == null)
            return true

        return backend.search(SearchFollowerRequest(
                userId = id,
                followerUserId = requestContext.currentUser()?.id
        )).followers.isEmpty()
    }

}
