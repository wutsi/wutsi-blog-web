package com.wutsi.blog.app.model

import com.wutsi.blog.client.story.StoryStatus
import java.util.Date

data class StoryModel(
        val id: Long = -1,
        val user: UserModel = UserModel(),
        val title: String? = null,
        val summary: String? = null,
        val thumbnailImage: HtmlImageModel? = null,
        val thumbnailUrl: String? = null,
        val sourceUrl: String? = null,
        val sourceSite: String? = null,
        val wordCount: Int = 0,
        val readabilityScore: Int = 0,
        val readingMinutes: Int = 0,
        val language: String? = null,
        val content: String? = null,
        val contentType: String? = null,
        val status: StoryStatus = StoryStatus.draft,
        val draft: Boolean = true,
        val published: Boolean = false,
        val creationDateTime: String = "",
        val modificationDateTime: String = "",
        val publishedDateTime: String = "",
        val publishedDateTimeAsDate: Date? = null,
        val modificationDateTimeISO8601: String = "",
        val publishedDateTimeISO8601: String? = null,
        val tags: List<TagModel> = emptyList(),
        val slug: String = "",
        val topic: TopicModel = TopicModel(),
        val live: Boolean = false,
        val liveDateTime: String = ""
)
