package com.wutsi.blog.app.page.calendar.model

import java.time.LocalDate

data class DayOfWeekModel(
    val name: String,
    val displayDate: String,
    val date: LocalDate,
    val posts: List<CalendarPostModel>,
    val storiesToPublish: List<CalendarStoryModel>,
    val newsletterDelivery: Boolean
)
