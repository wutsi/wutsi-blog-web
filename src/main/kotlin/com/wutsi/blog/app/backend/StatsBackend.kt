package com.wutsi.blog.app.backend

import com.wutsi.blog.client.stats.SearchDailyStatsRequest
import com.wutsi.blog.client.stats.SearchDailyStatsResponse
import com.wutsi.blog.client.stats.SearchMonthlyStatsStoryRequest
import com.wutsi.blog.client.stats.SearchMonthlyStatsStoryResponse
import com.wutsi.blog.client.stats.SearchMonthlyStatsUserRequest
import com.wutsi.blog.client.stats.SearchMonthlyStatsUserResponse
import com.wutsi.blog.client.stats.SearchMonthlyTrafficStoryRequest
import com.wutsi.blog.client.stats.SearchMonthlyTrafficStoryResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StatsBackend (private val http: Http) {
    @Value("\${wutsi.backend.stats.endpoint}")
    private lateinit var endpoint: String

    fun search(request: SearchDailyStatsRequest): SearchDailyStatsResponse {
        return http.post("$endpoint/search/daily", request, SearchDailyStatsResponse::class.java).body!!
    }

    fun search(request: SearchMonthlyStatsUserRequest): SearchMonthlyStatsUserResponse {
        return http.post("$endpoint/search/monthly/user", request, SearchMonthlyStatsUserResponse::class.java).body!!
    }

    fun search(request: SearchMonthlyStatsStoryRequest): SearchMonthlyStatsStoryResponse {
        return http.post("$endpoint/search/monthly/story", request, SearchMonthlyStatsStoryResponse::class.java).body!!
    }

    fun search(request: SearchMonthlyTrafficStoryRequest): SearchMonthlyTrafficStoryResponse {
        return http.post("$endpoint/search/monthly/traffic", request, SearchMonthlyTrafficStoryResponse::class.java).body!!
    }
}
