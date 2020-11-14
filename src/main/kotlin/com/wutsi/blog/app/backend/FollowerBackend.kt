package com.wutsi.blog.app.backend

import com.wutsi.blog.client.follow.CreateFollowerRequest
import com.wutsi.blog.client.follow.CreateFollowerResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FollowerBackend (private val http: Http) {
    @Value("\${wutsi.backend.follower.endpoint}")
    private lateinit var endpoint: String

    fun create(request: CreateFollowerRequest): CreateFollowerResponse {
        return http.post(endpoint, request, CreateFollowerResponse::class.java).body!!
    }

    fun delete(id: Long) {
        http.delete("$endpoint/$id")
    }
}
