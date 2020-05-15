package com.wutsi.blog.app.backend

import com.wutsi.blog.client.view.SearchViewRequest
import com.wutsi.blog.client.view.SearchViewResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ViewBackend (private val http: Http) {
    @Value("\${wutsi.backend.view.endpoint}")
    private lateinit var endpoint: String

    fun search(request: SearchViewRequest): SearchViewResponse {
        return http.post("$endpoint/search", request, SearchViewResponse::class.java).body!!
    }
}
