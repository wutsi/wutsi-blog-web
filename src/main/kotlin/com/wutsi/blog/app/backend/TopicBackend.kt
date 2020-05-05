package com.wutsi.blog.app.backend

import com.wutsi.blog.client.story.SearchTopicResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TopicBackend (private val http: Http) {
    @Value("\${wutsi.backend.topic.endpoint}")
    private lateinit var endpoint: String

    fun all(): SearchTopicResponse {
        return http.get(endpoint, SearchTopicResponse::class.java).body!!
    }
}
