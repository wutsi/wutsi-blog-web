package com.wutsi.blog.app.model

data class UserModel (
        val id: Long = -1,
        val name: String = "",
        val fullName: String = "",
        val email: String? = null,
        val loginCount: Long = 0,
        val pictureUrl: String? = null,
        val websiteUrl: String? = null,
        val biography: String? = null,
        val accounts: List<AccountModel> = emptyList(),
        val facebookUrl: String? = null,
        val twitterUrl: String? = null,
        val linkedInUrl: String? = null,
        val slug: String = ""
)
