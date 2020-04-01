package com.wutsi.blog.app.model

data class AccountModel (
        val id: Long = -1,
        val provider: String = "",
        val providerUserId: String? = null,
        val loginCount: Long = 0
)
