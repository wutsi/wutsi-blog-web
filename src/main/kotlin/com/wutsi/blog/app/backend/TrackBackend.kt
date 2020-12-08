package com.wutsi.blog.app.backend

import com.wutsi.blog.client.track.PushTrackRequest
import com.wutsi.blog.client.track.PushTrackResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TrackBackend(private val http: Http) {
    @Value("\${wutsi.backend.track.endpoint}")
    private lateinit var endpoint: String

    fun push(request: PushTrackRequest): PushTrackResponse {
        return http.post(endpoint, request, PushTrackResponse::class.java).body!!
    }
}
