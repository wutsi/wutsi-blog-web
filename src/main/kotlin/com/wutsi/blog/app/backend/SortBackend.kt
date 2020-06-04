package com.wutsi.blog.app.backend

import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.client.story.SortStoryResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SortBackend (private val http: Http) {
    @Value("\${wutsi.backend.sort.endpoint}")
    private lateinit var endpoint: String

    fun sort(request: SortStoryRequest): SortStoryResponse {
        return http.post(endpoint, request, SortStoryResponse::class.java).body!!
    }
}
