package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.PinModel
import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.blog.sdk.PinApi
import com.wutsi.core.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class PinService(
    private val api: PinApi,
    private val mapper: PinMapper,
    private val requestContext: RequestContext
) {
    fun create(storyId: Long) {
        val user = requestContext.currentUser()
            ?: return

        api.create(user.id, CreatePinRequest(storyId = storyId))
    }

    fun delete() {
        val user = requestContext.currentUser()
            ?: return

        api.delete(user.id)
    }

    fun get(): PinModel? {
        val user = requestContext.currentUser()
            ?: return null

        try {
            return mapper.toPinModel(api.get(user.id).pin)
        } catch (ex: NotFoundException) {
            return null
        }
    }
}
