package com.wutsi.blog.app.page.calendar.model

import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.StoryStatus.draft
import java.util.Date

data class CalendarStoryModel(
    val id: Long = -1,
    val title: String = "",
    var color: String = "",
    val status: StoryStatus = draft,
    val scheduledPublishDateTime: Date? = null
)
