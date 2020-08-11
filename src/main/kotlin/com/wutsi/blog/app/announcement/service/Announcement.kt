package com.wutsi.blog.app.announcement.service

interface Announcement {
    fun show(): Boolean

    fun name(): String

    fun actionUrl(): String?
}
