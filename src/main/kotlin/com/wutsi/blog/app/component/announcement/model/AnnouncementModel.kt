package com.wutsi.blog.app.component.announcement.model

data class AnnouncementModel (
        val name: String = "",
        val title: String = "",
        val message: String = "",
        val actionText: String? = null,
        val actionUrl: String? = null,
        val iconUrl: String? = null,
        val autoHide: Boolean = true,
        val delay: Int = 10000
)
