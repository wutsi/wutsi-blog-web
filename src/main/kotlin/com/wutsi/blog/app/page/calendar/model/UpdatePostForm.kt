package com.wutsi.blog.app.page.calendar.model

data class UpdatePostForm(
    val id: Long,
    val message: String = "",
    val scheduledDateTime: String = ""
)
