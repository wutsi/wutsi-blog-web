package com.wutsi.blog.app.page.mail.service

import com.wutsi.blog.app.backend.MailBackend
import com.wutsi.blog.client.mail.UnsubscribeMailRequest
import org.springframework.stereotype.Component


@Component
class MailService (
        private val backend: MailBackend
){
    fun unsubscribe(email: String, userId: Long?= null) {
        backend.unsubscribe(UnsubscribeMailRequest(
                subscriberEmail = email,
                userId = userId
        ))
    }
}
