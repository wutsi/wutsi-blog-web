package com.wutsi.blog.app.backend

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.client.wpp.GetPartnerResponse
import com.wutsi.blog.client.wpp.SavePartnerRequest
import com.wutsi.blog.client.wpp.SavePartnerResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PartnerBackend (
        private val http: Http,
        private val requestContext: RequestContext
) {
    @Value("\${wutsi.backend.partner.endpoint}")
    private lateinit var endpoint: String

    fun get(): GetPartnerResponse {
        val id = requestContext.currentUser()?.id
        return http.get("$endpoint/user/$id", GetPartnerResponse::class.java).body!!
    }

    fun save(request: SavePartnerRequest): SavePartnerResponse {
        val id = requestContext.currentUser()?.id
        return http.post("$endpoint/user/$id", request, SavePartnerResponse::class.java).body
    }
}
