package com.wutsi.blog.app.model

data class PageModel (
        val name: String = "",
        val title: String = "",
        val description: String = "",
        val type: String = "website",
        val url: String? = null,
        val imageUrl: String? = null,
        val modifiedTime: String? = null,
        val publishedTime: String? = null,
        val author: String? = null,
        val robots: String = "all",
        val tags: List<String> = emptyList(),
        val baseUrl: String = "",
        val twitterUserId: String? = null
)

