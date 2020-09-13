package com.wutsi.blog.app.backend

import com.wutsi.blog.client.like.*
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class LikeBackend(private val http: Http) {
    @Value("\${wutsi.backend.like.endpoint}")
    private lateinit var endpoint: String

    fun search(request: SearchLikeRequest): SearchLikeResponse {
        return http.post("$endpoint/search", request, SearchLikeResponse::class.java).body!!
    }

    fun count(request: CountLikeRequest): CountLikeResponse {
        return http.post("$endpoint/count", request, CountLikeResponse::class.java).body!!
    }

    fun create(request: CreateLikeRequest): CreateLikeResponse {
        return http.post("$endpoint", request, CreateLikeResponse::class.java).body!!
    }

    fun delete(id: Long) {
        return http.delete("$endpoint/$id")
    }
}