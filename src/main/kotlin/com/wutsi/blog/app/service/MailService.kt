package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.MailBackend
import com.wutsi.blog.client.mail.UnsubscribeMailRequest
import org.springframework.stereotype.Component


@Component
class MailService (
        private val backend: MailBackend
){
    fun unsubscribe(email: String) {
        backend.unsubscribe(UnsubscribeMailRequest(email = email))
    }
}
