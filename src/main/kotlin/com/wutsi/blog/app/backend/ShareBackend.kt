package com.wutsi.blog.app.backend

import com.wutsi.blog.client.share.CreateShareRequest
import com.wutsi.blog.client.share.CreateShareResponse
import com.wutsi.blog.client.user.AuthenticateRequest
import com.wutsi.blog.client.user.AuthenticateResponse
import com.wutsi.blog.client.user.GetSessionResponse
import com.wutsi.blog.client.user.RunAsRequest
import com.wutsi.blog.client.user.RunAsResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ShareBackend (private val http: Http) {
    @Value("\${wutsi.backend.share.endpoint}")
    private lateinit var endpoint: String

    fun create(request: CreateShareRequest): CreateShareResponse {
        return http.post(endpoint, request, CreateShareResponse::class.java).body!!
    }
}
