package com.wutsi.blog.app.model

data class OAuth2UserModel (
        val id: String = "",
        val fullName: String = "",
        val email: String? = null,
        val pictureUrl: String? = null,
        val provider: String = ""
)
