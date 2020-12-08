package com.wutsi.blog.app.component.share.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.share.CreateShareRequest
import com.wutsi.blog.sdk.ShareApi
import org.springframework.stereotype.Service

@Service
class ShareService(
    private val api: ShareApi,
    private val requestContext: RequestContext
) {
    fun create(storyId: Long, target: String): Long = api.create(
        CreateShareRequest(
            storyId = storyId,
            target = target,
            deviceId = requestContext.deviceId(),
            userId = requestContext.currentUser()?.id
        )
    ).shareId
}
