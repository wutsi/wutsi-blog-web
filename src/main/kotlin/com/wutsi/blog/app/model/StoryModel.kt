package com.wutsi.blog.app.model

data class StoryModel(
        val id: Long? = -1,
        val userId: Long = -1,
        val title: String? = null,
        val summary: String? = null,
        val thumbmailUrl: String? = null,
        val sourceUrl: String? = null,
        val wordCount: Int = 0,
        val readingMinutes: Int = 0,
        val language: String? = null,
        val content: String? = null,
        val contentType: String? = null,
        val draft: Boolean = true,
        val published: Boolean = false,
        val creationDateTime: String = "",
        val modificationDateTime: String = "",
        val publishedDateTime: String = ""
)
