package com.wutsi.blog.app.backend

import com.wutsi.blog.client.user.AuthenticateRequest
import com.wutsi.blog.client.user.AuthenticateResponse
import com.wutsi.http.Http
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AuthenticationBackend (private val http: Http) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AuthenticationBackend::class.java)
    }

    @Value("\${wutsi.backend.authentication.endpoint}")
    private lateinit var endpoint: String;

    fun login(request: AuthenticateRequest): AuthenticateResponse {
        return http.post(endpoint, request, AuthenticateResponse::class.java).body
    }

    fun logout(token: String) {
        val url = "$endpoint/$token"
        http.delete(url)
    }

    @Async
    fun logoutAsync(token: String) {
        logout(token)
    }
}
