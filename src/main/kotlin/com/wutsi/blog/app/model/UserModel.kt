package com.wutsi.blog.app.model

data class UserModel (
        val id: Long = -1,
        val fullName: String = "",
        val email: String? = null,
        val pictureUrl: String? = null,
        val twitterUserId: String? = null,
        val biography: String? = null
)
