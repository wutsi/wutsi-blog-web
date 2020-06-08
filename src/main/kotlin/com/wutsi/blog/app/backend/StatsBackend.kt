package com.wutsi.blog.app.backend

import com.wutsi.blog.client.stats.SearchStatsRequest
import com.wutsi.blog.client.stats.SearchStatsResponse
import com.wutsi.blog.client.stats.SearchStatsStoryRequest
import com.wutsi.blog.client.stats.SearchStatsStoryResponse
import com.wutsi.blog.client.stats.SearchStatsUserRequest
import com.wutsi.blog.client.stats.SearchStatsUserResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StatsBackend (private val http: Http) {
    @Value("\${wutsi.backend.stats.endpoint}")
    private lateinit var endpoint: String

    fun search(request: SearchStatsRequest): SearchStatsResponse {
        return http.post("$endpoint/search", request, SearchStatsResponse::class.java).body!!
    }

    fun search(request: SearchStatsUserRequest): SearchStatsUserResponse {
        return http.post("$endpoint/search/user", request, SearchStatsUserResponse::class.java).body!!
    }

    fun search(request: SearchStatsStoryRequest): SearchStatsStoryResponse {
        return http.post("$endpoint/search/story", request, SearchStatsStoryResponse::class.java).body!!
    }
}
