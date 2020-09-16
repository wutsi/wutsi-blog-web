package com.wutsi.blog.app.component.announcement.service

interface Announcement {
    fun show(): Boolean

    fun name(): String

    fun actionUrl(): String?

    fun cookieMaxAge() : Int = 86400
}
