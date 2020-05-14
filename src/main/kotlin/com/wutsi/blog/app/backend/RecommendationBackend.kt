package com.wutsi.blog.app.backend

import com.wutsi.blog.client.story.RecommendStoryRequest
import com.wutsi.blog.client.story.RecommendStoryResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RecommendationBackend (private val http: Http) {
    @Value("\${wutsi.backend.recommendation.endpoint}")
    private lateinit var endpoint: String

    fun search(request: RecommendStoryRequest): RecommendStoryResponse {
        return http.post("$endpoint/search", request, RecommendStoryResponse::class.java).body!!
    }
}
