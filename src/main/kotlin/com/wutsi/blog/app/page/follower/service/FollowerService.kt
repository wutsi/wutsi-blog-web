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
    fun follow(blogId: Long): Long = backend.create(CreateFollowerRequest(
            userId = blogId,
            followerUserId = requestContext.currentUser()?.id
        )).followerId

    fun findFollwerId(id: Long): Long? {
        requestContext.currentUser()
                ?: return null

        try {
            val followers = backend.search(SearchFollowerRequest(
                    userId = id,
                    followerUserId = requestContext.currentUser()?.id
            )).followers
            return if (followers.size == 0) null else followers[0].id
        }catch(ex: Exception) {
            return null
        }
    }

}
