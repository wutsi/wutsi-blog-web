package com.wutsi.blog.app.page.calendar.model

import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.StoryStatus.draft
import java.util.Date

data class CalendarStoryModel(
    val id: Long = -1,
    val title: String = "",
    var color: String = "",
    val status: StoryStatus = draft,
    val publishDateTime: Date? = null,
    val published: Boolean = false,
    val scheduledPublishDateTime: Date? = null,
    val publishedDateTime: Date? = null,
    val url: String = "",
    val readUrl: String = "",
    val scheduledPublishDateTimeText: String = "",
    val publishedDateTimeText: String = ""
)
