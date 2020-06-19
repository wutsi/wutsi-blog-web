package com.wutsi.blog.app.backend

import com.wutsi.blog.client.mail.UnsubscribeMailRequest
import com.wutsi.blog.client.mail.UnsubscribeMailResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MailBackend (private val http: Http) {
    @Value("\${wutsi.backend.mail.endpoint}")
    private lateinit var endpoint: String

    fun unsubscribe(request: UnsubscribeMailRequest): UnsubscribeMailResponse {
        return http.post("$endpoint/unsubscribe", request, UnsubscribeMailResponse::class.java).body!!
    }
}
