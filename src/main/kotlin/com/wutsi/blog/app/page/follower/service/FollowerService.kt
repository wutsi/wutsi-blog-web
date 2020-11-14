package com.wutsi.blog.app.page.follower.service

import com.wutsi.blog.app.backend.FollowerBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.follow.CreateFollowerRequest
import org.springframework.stereotype.Service

@Service
class FollowerService(
        private val backend: FollowerBackend,
        private val requestContext: RequestContext
) {
    fun follow(blogId: Long): Long = backend.create(CreateFollowerRequest(
            userId = blogId,
            followerId = requestContext.currentUser()?.id
        )).followerId

    fun findFollwerId(id: Long): Long? =
            if (requestContext.currentUser() == null)
                null
            else
                if (Math.random() < .5) null else 1L

}
