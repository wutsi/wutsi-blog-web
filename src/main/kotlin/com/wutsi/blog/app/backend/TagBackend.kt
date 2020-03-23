package com.wutsi.blog.app.backend

import com.wutsi.blog.client.story.SearchTagResponse
import com.wutsi.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TagBackend (private val http: Http) {
    @Value("\${wutsi.backend.tag.endpoint}")
    private lateinit var endpoint: String

    fun search(query:String): SearchTagResponse {
        return http.get("$endpoint/search?query=$query", SearchTagResponse::class.java).body
    }
}
