package com.wutsi.blog.app.component.announcement.service

import com.wutsi.blog.app.util.CookieHelper

interface Announcement {
    fun show(): Boolean

    fun name(): String

    fun actionUrl(): String?

    fun cookieMaxAge() : Int = CookieHelper.ONE_DAY_SECONDS
}
