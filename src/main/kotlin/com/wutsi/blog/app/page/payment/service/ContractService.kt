package com.wutsi.blog.app.page.payment.service

import com.wutsi.blog.app.backend.ContractBackend
import org.springframework.stereotype.Service

@Service
class ContractService(
    private val backend: ContractBackend
) {
    fun hasContract(): Boolean {
        try {
            backend.get()
            return true
        } catch (ex: Exception) {
            return false
        }
    }
}
