package com.wutsi.blog.app.page.mail

import com.wutsi.blog.app.page.track.model.PushTrackForm
import com.wutsi.blog.app.page.track.service.TrackService
import com.wutsi.blog.app.util.PageName
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.Clock
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/mail/track")
class MailTrackingController(
    private val trackService: TrackService,
    private val clock: Clock
) {
    companion object {
        const val REFERER = "https://pixel.mail.wutsi.com"
        private val LOGGER = LoggerFactory.getLogger(MailTrackingController::class.java)
    }

    @GetMapping("/{storyId}.png", produces = ["image/png"])
    fun read(
        @PathVariable storyId: Long,
        @RequestHeader("User-Agent", required = false) userAgent: String? = null,
        @RequestParam("u", required = false) userId: Long? = null,
        @RequestParam("d", required = false) durationMinutes: Int? = null,
        @RequestParam("c", required = false) campaign: String? = null,
        @RequestParam("hid", required = false) hitId: String? = null,
        @RequestParam("did", required = false) deviceId: String? = null,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        try {
            val png = MailTrackingController::class.java.getResourceAsStream("/pixel/img.png")
            response.contentType = "image/png"
            response.addHeader("Cache-Control", "no-cache, max-age=0")
            response.addHeader("Pragma", "no-cache")
            IOUtils.copy(png, response.outputStream)
        } finally {
            try {
                val url = request.requestURL.toString() + (request.queryString?.let { "?$it" } ?: "")
                trackPixel(
                    storyId = storyId,
                    userAgent = userAgent,
                    userId = userId,
                    durationMinutes = durationMinutes,
                    hitId = hitId ?: UUID.randomUUID().toString(),
                    deviceId = deviceId ?: UUID.randomUUID().toString(),
                    url = url,
                )
            } catch (ex: Exception) {
                LOGGER.warn("Unable to read of Story#$storyId", ex)
            }
        }
    }

    @GetMapping("/link")
    fun link(
        @RequestParam url: String,
        @RequestHeader("User-Agent", required = false) userAgent: String? = null,
        @RequestParam("s", required = false) storyId: Long? = null,
        @RequestParam("u", required = false) userId: Long? = null,
        @RequestParam("hid", required = false) hitId: String? = null,
        @RequestParam("did", required = false) deviceId: String? = null,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        try {
            response.sendRedirect(url)
        } finally {
            try {
                trackLink(
                    storyId = storyId,
                    userAgent = userAgent,
                    userId = userId,
                    hitId = hitId ?: UUID.randomUUID().toString(),
                    deviceId = deviceId ?: UUID.randomUUID().toString(),
                    url = url
                )
            } catch (ex: Exception) {
                LOGGER.warn("Unable to track click to $url", ex)
            }
        }
    }

    private fun trackPixel(
        storyId: Long?,
        userAgent: String?,
        userId: Long?,
        durationMinutes: Int? = null,
        hitId: String,
        deviceId: String,
        url: String
    ) {
        val time = clock.millis()

        trackService.push(
            PushTrackForm(
                time = time,
                hid = hitId,
                duid = deviceId,
                pid = storyId?.toString() ?: "",
                uid = userId?.toString() ?: "",
                page = PageName.READ,
                event = "click",
                ua = userAgent,
                referer = REFERER,
                url = url
            )
        )

        trackService.push(
            PushTrackForm(
                time = time,
                hid = hitId,
                duid = deviceId,
                pid = storyId?.toString() ?: "",
                uid = userId?.toString() ?: "",
                page = PageName.READ,
                event = "readstart",
                ua = userAgent,
                referer = REFERER,
                url = url
            )
        )

        if (durationMinutes != null) {
            trackService.push(
                PushTrackForm(
                    time = (time + durationMinutes * 60 * 1000),
                    hid = hitId,
                    duid = deviceId,
                    pid = storyId?.toString() ?: "",
                    uid = userId?.toString() ?: "",
                    page = PageName.READ,
                    event = "scroll",
                    value = "100",
                    ua = userAgent,
                    referer = REFERER,
                    url = url
                )
            )
        }
    }

    private fun trackLink(
        storyId: Long?,
        userAgent: String?,
        userId: Long?,
        hitId: String,
        deviceId: String,
        url: String
    ) {
        val time = clock.millis()

        trackService.push(
            PushTrackForm(
                time = time,
                hid = hitId,
                duid = deviceId,
                pid = storyId?.toString() ?: "",
                uid = userId?.toString() ?: "",
                page = PageName.READ,
                event = "link",
                ua = userAgent,
                referer = REFERER,
                url = url
            )
        )
    }
}
