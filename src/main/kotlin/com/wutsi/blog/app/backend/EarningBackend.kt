package com.wutsi.blog.app.backend

import com.wutsi.blog.client.payment.SearchEarningRequest
import com.wutsi.blog.client.payment.SearchEarningResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EarningBackend (
        private val http: Http,
        @Value("\${wutsi.backend.earning.endpoint}") private var endpoint: String
) {
    fun search(request: SearchEarningRequest): SearchEarningResponse {
        return http.post("$endpoint/search", request, SearchEarningResponse::class.java).body!!
    }
}
