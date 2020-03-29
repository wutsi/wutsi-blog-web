package com.wutsi.blog.app.model

data class OpenGraphModel (
        val title: String = "",
        val description: String = "",
        val type: String = "website",
        val url: String = "",
        val imageUrl: String? = null,
        val publishedTime: String = "",
        val author: String = ""
)

