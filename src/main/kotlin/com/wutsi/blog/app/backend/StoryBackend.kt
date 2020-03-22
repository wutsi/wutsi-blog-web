package com.wutsi.blog.app.backend

import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryResponse
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SearchStoryResponse
import com.wutsi.http.Http
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StoryBackend (private val http: Http) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryBackend::class.java)
    }

    @Value("\${wutsi.backend.story.endpoint}")
    private lateinit var endpoint: String

    fun create(request: SaveStoryRequest): SaveStoryResponse {
        return http.post(endpoint, request, SaveStoryResponse::class.java).body
    }

    fun update(id: Long, request: SaveStoryRequest): SaveStoryResponse {
        return http.post("$endpoint/$id", request, SaveStoryResponse::class.java).body
    }

    fun get(id:Long): GetStoryResponse {
        return http.get("$endpoint/$id", GetStoryResponse::class.java).body
    }

    fun search(request: SearchStoryRequest): SearchStoryResponse {
        return http.post("$endpoint/search", request, SearchStoryResponse::class.java).body
    }

    fun count(request: SearchStoryRequest): CountStoryResponse {
        return http.post("$endpoint/count", request, CountStoryResponse::class.java).body
    }
}
