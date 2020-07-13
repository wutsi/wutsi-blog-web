package com.wutsi.blog.app.backend

import com.wutsi.blog.client.channel.CreateChannelRequest
import com.wutsi.blog.client.channel.CreateChannelResponse
import com.wutsi.blog.client.channel.GetChannelResponse
import com.wutsi.blog.client.channel.SearchChannelRequest
import com.wutsi.blog.client.channel.SearchChannelResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ChannelBackend (
        private val http: Http,
        @Value("\${wutsi.backend.channel.endpoint}") private var endpoint: String
) {

    fun create(request: CreateChannelRequest): CreateChannelResponse {
        return http.post(endpoint, request, CreateChannelResponse::class.java).body!!
    }

    fun get(id:Long): GetChannelResponse {
        return http.get("$endpoint/$id", GetChannelResponse::class.java).body!!
    }

    fun search(request: SearchChannelRequest): SearchChannelResponse {
        return http.post("$endpoint/search", request, SearchChannelResponse::class.java).body!!
    }

    fun delete(id: Long) {
        return http.delete("$endpoint/$id")
    }
}
