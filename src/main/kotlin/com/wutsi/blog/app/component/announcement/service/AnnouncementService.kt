package com.wutsi.blog.app.component.announcement.service

import com.wutsi.blog.app.component.announcement.model.AnnouncementModel
import com.wutsi.blog.app.component.announcement.model.AnnouncementResponse
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Value
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AnnouncementService(
        private val announcements: List<Announcement>,
        private val logger: KVLogger
) {
    fun get(page: String, request: HttpServletRequest, response: HttpServletResponse): AnnouncementResponse {
        val announcement = announcements.find { shouldDisplay(page, it, request) }
        if (announcement == null) {
            logger.add("AnnouncementVisible", false)
            return AnnouncementResponse()
        } else {
            storeIntoCookie(announcement, request, response)

            logger.add("AnnouncementVisible", true)
            logger.add("AnnouncementName", announcement.name())
            logger.add("AnnouncementURL", announcement.actionUrl())
            return AnnouncementResponse(
                show = true,
                announcement = AnnouncementModel(
                        name = announcement.name(),
                        title = announcement.title(),
                        message = announcement.description(),
                        actionText = announcement.actionText(),
                        actionUrl = announcement.actionUrl(),
                        iconUrl = announcement.iconUrl(),
                        autoHide = announcement.autoHide(),
                        delay = announcement.delay()
                )
            )
        }
    }

    private fun shouldDisplay(page: String, announcement: Announcement, request: HttpServletRequest): Boolean {
        return !hasCookie(announcement, request) && announcement.show(page)
    }

    private fun hasCookie(announcement: Announcement, request: HttpServletRequest): Boolean {
        val cookieName = cookie(announcement)
        val value = CookieHelper.get(cookieName, request)
        return value != null
    }

    private fun storeIntoCookie(announcement: Announcement, request: HttpServletRequest, response: HttpServletResponse) {
        val cookieName = cookie(announcement)
        val value = System.currentTimeMillis().toString()
        CookieHelper.put(cookieName, value, request, response, announcement.cookieMaxAge())
    }

    private fun cookie(announcement: Announcement) = "__w_announce_" + announcement.name()

}
