package com.wutsi.blog.app.page.payment.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.sdk.ContractApi
import org.springframework.stereotype.Service

@Service
class ContractService(
    private val api: ContractApi,
    private val requestContext: RequestContext
) {
    fun hasContract(): Boolean {
        val user = requestContext.currentUser()
            ?: return false

        try {
            api.get(user.id)
            return true
        } catch (ex: Exception) {
            return false
        }
    }
}
