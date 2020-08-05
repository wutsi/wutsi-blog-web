package com.wutsi.blog.app.backend

import com.wutsi.blog.client.channel.CreatePushSubscriptionRequest
import com.wutsi.blog.client.channel.CreatePushSubscriptionResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PushSubscriptionBackend (private val http: Http) {
    @Value("\${wutsi.backend.push_subscription.endpoint}")
    private lateinit var endpoint: String

    fun create(request: CreatePushSubscriptionRequest): CreatePushSubscriptionResponse {
        return http.post(endpoint, request, CreatePushSubscriptionResponse::class.java).body!!
    }
}
