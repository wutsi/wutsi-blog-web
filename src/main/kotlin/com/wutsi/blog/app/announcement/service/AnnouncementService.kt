package com.wutsi.blog.app.announcement.service

import com.wutsi.blog.app.announcement.model.AnnouncementModel
import com.wutsi.blog.app.announcement.model.AnnouncementResponse
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.util.CookieHelper
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class AnnouncementService(
        private val requestContext: RequestContext,
        private val announcements: List<Announcement>
) {
    companion object {
        private const val ONE_DAY_SECONDS = 84600
    }
    fun get(page: String, request: HttpServletRequest, response: HttpServletResponse): AnnouncementResponse {
        val announcement = announcements.find { shouldDisplay(page, it, request) }
        if (announcement == null) {
            return AnnouncementResponse()
        } else {
            val name = announcement.name()
            storeIntoCookie(announcement, request, response)

            return AnnouncementResponse(
                        show = true,
                        announcement = AnnouncementModel(
                        name = name,
                        message = requestContext.getMessage("announcement.${name}.message"),
                        actionUrl = announcement.actionUrl()
                )
            )
        }
    }

    private fun shouldDisplay(page: String, announcement: Announcement, request: HttpServletRequest): Boolean {
        return !hasCookie(announcement, request) && announcement.supports(page) && announcement.show()
    }

    private fun hasCookie(announcement: Announcement, request: HttpServletRequest): Boolean {
        val cookieName = cookie(announcement)
        val value = CookieHelper.get(cookieName, request)
        return value != null
    }

    private fun storeIntoCookie(announcement: Announcement, request: HttpServletRequest, response: HttpServletResponse) {
        val cookieName = cookie(announcement)
        val value = System.currentTimeMillis().toString()
        CookieHelper.put(cookieName, value, request, response, ONE_DAY_SECONDS)
    }

    private fun cookie(announcement: Announcement) = "__w_announce_" + announcement.name()

}
