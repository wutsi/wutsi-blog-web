package com.wutsi.blog.app.backend

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.client.payment.GetContractResponse
import com.wutsi.core.http.Http
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ContractBackend(
    private val http: Http,
    private val requestContext: RequestContext
) {
    @Value("\${wutsi.backend.contract.endpoint}")
    private lateinit var endpoint: String

    fun get(): GetContractResponse {
        val id = requestContext.currentUser()?.id
        return http.get("$endpoint/user/$id", GetContractResponse::class.java).body!!
    }
}
