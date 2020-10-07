package com.wutsi.blog.app.component.announcement.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.util.CookieHelper

interface Announcement {
    companion object{
        const val MAX_LOGIN = 5L
    }

    fun show(): Boolean

    fun name(): String

    fun description(): String

    fun actionUrl(): String?

    fun iconUrl(): String? = null

    fun cookieMaxAge() : Int = CookieHelper.ONE_DAY_SECONDS
}
