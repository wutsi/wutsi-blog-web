package com.wutsi.blog.app.model

data class PublishForm (
        val id: Long = -1,
        val tags: List<String> = emptyList()
)
