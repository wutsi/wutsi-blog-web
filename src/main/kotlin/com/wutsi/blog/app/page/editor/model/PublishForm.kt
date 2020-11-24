package com.wutsi.blog.app.page.editor.model

data class PublishForm (
        val id: Long = -1,
        val title: String = "",
        val tagline: String = "",
        val summary: String = "",
        val topicId: String = "",
        val tags: List<String> = emptyList(),
        val socialMediaMessage: String = ""
)
