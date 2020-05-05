package com.wutsi.blog.app.model

data class PublishForm (
        val id: Long = -1,
        val topicId: String = "",
        val tags: List<String> = emptyList()
)
