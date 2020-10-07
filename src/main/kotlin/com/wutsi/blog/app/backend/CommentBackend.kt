package com.wutsi.blog.app.backend

import com.wutsi.blog.client.comment.CountCommentResponse
import com.wutsi.blog.client.comment.CreateCommentRequest
import com.wutsi.blog.client.comment.CreateCommentResponse
import com.wutsi.blog.client.comment.SearchCommentRequest
import com.wutsi.blog.client.comment.SearchCommentResponse
import com.wutsi.blog.client.comment.UpdateCommentRequest
import com.wutsi.blog.client.comment.UpdateCommentResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CommentBackend (private val http: Http) {
    @Value("\${wutsi.backend.comment.endpoint}")
    private lateinit var endpoint: String

    fun count(request: SearchCommentRequest): CountCommentResponse {
        return http.post("$endpoint/count", request, CountCommentResponse::class.java).body!!
    }

    fun search(request: SearchCommentRequest): SearchCommentResponse {
        return http.post("$endpoint/search", request, SearchCommentResponse::class.java).body!!
    }

    fun create(request: CreateCommentRequest): CreateCommentResponse {
        return http.post(endpoint, request, CreateCommentResponse::class.java).body!!
    }

    fun update(id: Long, request: UpdateCommentRequest): UpdateCommentResponse {
        return http.post("$endpoint/$id", request, UpdateCommentResponse::class.java).body!!
    }

    fun delete(id: Long) {
        return http.delete("$endpoint/$id")
    }
}
