package com.wutsi.blog.app.backend

import com.wutsi.blog.client.user.AuthenticateRequest
import com.wutsi.blog.client.user.AuthenticateResponse
import com.wutsi.blog.client.user.GetSessionResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AuthenticationBackend (private val http: Http) {
    @Value("\${wutsi.backend.authentication.endpoint}")
    private lateinit var endpoint: String

    fun login(request: AuthenticateRequest): AuthenticateResponse {
        return http.post(endpoint, request, AuthenticateResponse::class.java).body
    }

    fun logout(token: String) {
        val url = "$endpoint/$token"
        http.delete(url)
    }

    fun session(token: String): GetSessionResponse {
        return http.get("$endpoint/$token", GetSessionResponse::class.java).body
    }
}
