package com.wutsi.blog.app.backend

import com.wutsi.blog.client.channel.CreateChannelRequest
import com.wutsi.blog.client.channel.CreateChannelResponse
import com.wutsi.blog.client.channel.GetChannelResponse
import com.wutsi.blog.client.channel.SearchChannelRequest
import com.wutsi.blog.client.channel.SearchChannelResponse
import com.wutsi.blog.client.pin.CreatePinRequest
import com.wutsi.blog.client.pin.CreatePinResponse
import com.wutsi.blog.client.pin.SearchPinResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PinBackend (
        private val http: Http,
        @Value("\${wutsi.backend.pin.endpoint}") private var endpoint: String
) {
    fun create(request: CreatePinRequest): CreatePinResponse {
        return http.post(endpoint, request, CreatePinResponse::class.java).body!!
    }

    fun search(
            userId: Long,
            limit: Int = 20,
            offset: Int = 0
    ): SearchPinResponse {
        val url = "$endpoint?userId=$userId&limit=$limit&offset=$offset"
        return http.get(url, SearchPinResponse::class.java).body!!
    }

    fun delete(id: Long) {
        return http.delete("$endpoint/$id")
    }
}
