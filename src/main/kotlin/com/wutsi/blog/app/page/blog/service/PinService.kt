package com.wutsi.blog.app.page.blog.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.blog.model.PinModel
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.blog.client.pin.PinDto
import com.wutsi.blog.sdk.PinApi
import com.wutsi.core.exception.NotFoundException
import com.wutsi.core.util.DateUtils
import org.springframework.stereotype.Service
import java.util.Date

@Service
class PinService(
    private val api: PinApi,
    private val mapper: PinMapper,
    private val requestContext: RequestContext
) {
    companion object {
        const val MAX_DURATION_DAYS: Int = 7
    }

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

    fun get(user: UserModel): PinModel? {
        try {
            val pin = api.get(user.id).pin
            if (expired(pin)) {
                return null
            }

            return mapper.toPinModel(pin)
        } catch (ex: NotFoundException) {
            return null
        }
    }

    private fun expired(pin: PinDto): Boolean {
        val threshold = DateUtils.addDays(Date(), -MAX_DURATION_DAYS)
        return pin.creationDateTime.before(threshold)
    }
}
