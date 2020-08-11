package com.wutsi.blog.app.announcement.service

interface Announcement {
    fun supports(pageName: String): Boolean

    fun show(): Boolean

    fun name(): String

    fun actionUrl(): String?
}
