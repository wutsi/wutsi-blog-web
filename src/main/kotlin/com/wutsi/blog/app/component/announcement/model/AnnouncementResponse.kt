package com.wutsi.blog.app.component.announcement.model

data class AnnouncementResponse (
        val show: Boolean = false,
        val announcement: AnnouncementModel = AnnouncementModel()
)
