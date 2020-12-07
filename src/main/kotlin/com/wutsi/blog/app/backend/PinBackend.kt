package com.wutsi.blog.app.backend

import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.blog.client.pin.CreatePinResponse
import com.wutsi.blog.client.pin.GetPinResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PinBackend (
        private val http: Http,
        @Value("\${wutsi.backend.base-url}") private var baseUrl: String
) {
    fun create(userId: Long, request: CreatePinRequest): CreatePinResponse {
        return http.post(uri(userId), request, CreatePinResponse::class.java).body!!
    }

    fun get(userId: Long): GetPinResponse {
        return http.get(uri(userId), GetPinResponse::class.java).body!!
    }

    fun delete(userId: Long) {
        return http.delete(uri(userId))
    }

    private fun uri(userId: Long): String =
            "$baseUrl/v1/users/${userId}/pin"
}
