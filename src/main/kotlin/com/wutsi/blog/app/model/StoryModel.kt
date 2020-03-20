package com.wutsi.blog.app.model

import java.util.Date

data class StoryModel(
        val id: Long? = -1,
        val userId: Long = -1,
        val title: String? = null,
        val summary: String? = null,
        val thumbmailUrl: String? = null,
        val sourceUrl: String? = null,
        val worldCount: Int = 0,
        val readingMinutes: Int = 0,
        val language: String? = null,
        val content: String? = null,
        val contentType: String? = null,
        val draft: Boolean = true,
        val creationDateTime: Date = Date(),
        val modificationDateTime: Date = Date(),
        val publishedDateTime: Date? = null
)
