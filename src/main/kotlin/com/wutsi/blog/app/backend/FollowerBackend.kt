package com.wutsi.blog.app.backend

import com.wutsi.blog.client.follower.CreateFollowerRequest
import com.wutsi.blog.client.follower.CreateFollowerResponse
import com.wutsi.blog.client.follower.SearchFollowerRequest
import com.wutsi.blog.client.follower.SearchFollowerResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FollowerBackend (private val http: Http) {
    @Value("\${wutsi.backend.follower.endpoint}")
    private lateinit var endpoint: String

    fun create(request: CreateFollowerRequest): CreateFollowerResponse =
        http.post(endpoint, request, CreateFollowerResponse::class.java).body!!


    fun search(request: SearchFollowerRequest): SearchFollowerResponse =
         http.post("$endpoint/search", request, SearchFollowerResponse::class.java).body!!

    fun delete(id: Long) {
        http.delete("$endpoint/$id")
    }
}
