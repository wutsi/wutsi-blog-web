package com.wutsi.blog.app.model

data class OpenGraphModel (
        val title: String = "",
        val description: String = "",
        val type: String = "website",
        val url: String? = null,
        val imageUrl: String? = null,
        val modifiedTime: String? = null,
        val publishedTime: String? = null,
        val author: String? = null,
        val tags: List<String> = emptyList()
)

