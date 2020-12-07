package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.backend.PinBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.PinModel
import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.core.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class PinService(
        private val backend: PinBackend,
        private val mapper: PinMapper,
        private val requestContext: RequestContext
) {
    fun create(storyId: Long) {
        val user = requestContext.currentUser()
                ?: return

        backend.create(user.id, CreatePinRequest(storyId = storyId))
    }

    fun delete() {
        val user = requestContext.currentUser()
                ?: return

        backend.delete(user.id)
    }

    fun latest(): PinModel? {
        val user = requestContext.currentUser()
                ?: return null

        try {
            return mapper.toPinModel(backend.get(userId = user.id).pin)
        } catch (ex: NotFoundException) {
            return null
        }
    }
}
