package com.wutsi.blog.app.component.share.service

import com.wutsi.blog.app.backend.LikeBackend
import com.wutsi.blog.app.backend.ShareBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.component.like.model.LikeCountModel
import com.wutsi.blog.app.component.like.model.LikeModel
import com.wutsi.blog.client.like.CreateLikeRequest
import com.wutsi.blog.client.like.LikeDto
import com.wutsi.blog.client.like.SearchLikeRequest
import com.wutsi.blog.client.share.CreateShareRequest
import org.springframework.stereotype.Service

@Service
class ShareService(
        private val backend: ShareBackend,
        private val requestContext: RequestContext
) {
    fun create(storyId: Long, target: String): Long = backend.create(CreateShareRequest(
                storyId = storyId,
                target = target,
                deviceId = requestContext.deviceId(),
                userId = requestContext.currentUser()?.id
        )).shareId
}
