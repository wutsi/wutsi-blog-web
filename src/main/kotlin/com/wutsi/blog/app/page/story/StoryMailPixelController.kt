package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.page.track.model.PushTrackForm
import com.wutsi.blog.app.page.track.service.TrackService
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.Clock
import java.util.UUID
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/story/pixel")
class StoryMailPixelController(
    private val trackService: TrackService,
    private val clock: Clock
) {
    companion object {
        val PAGE = "page.read"
        val REFERER = "pixel.mail.wutsi.com"
    }

    @GetMapping("/{storyId}.png", produces = ["image/png"])
    fun pixel(
        @PathVariable storyId: Long,
        @RequestHeader("User-Agent", required = false) userAgent: String? = null,
        @RequestParam("u", required = false) userId: Long? = null,
        @RequestParam("d", required = false) durationMinutes: Int? = null,
        @RequestParam("c", required = false) campaign: String? = null,
        response: HttpServletResponse
    ) {
        try {
            val png = StoryMailPixelController::class.java.getResourceAsStream("/pixel/img.png")
            response.contentType = "image/png"
            response.addHeader("Cache-Control", "no-cache, max-age=0")
            response.addHeader("Pragma", "no-cache")
            IOUtils.copy(png, response.outputStream)
        } finally {
            track(storyId, userAgent, userId, durationMinutes, campaign)
        }
    }

    private fun track(
        storyId: Long,
        userAgent: String?,
        userId: Long?,
        durationMinutes: Int?,
        campaign: String?
    ) {
        val hitId = UUID.randomUUID().toString()
        val deviceId = UUID.randomUUID().toString()
        val time = clock.millis()

        trackService.push(
            PushTrackForm(
                time = time,
                hid = hitId,
                duid = deviceId,
                pid = storyId.toString(),
                uid = userId?.toString(),
                page = PAGE,
                event = "readstart",
                ua = userAgent,
                referer = REFERER
            )
        )

        if (durationMinutes != null) {
            trackService.push(
                PushTrackForm(
                    time = (time + durationMinutes * 60 * 1000),
                    hid = hitId,
                    duid = deviceId,
                    pid = storyId.toString(),
                    uid = userId.toString(),
                    page = PAGE,
                    event = "scroll",
                    value = "100",
                    ua = userAgent,
                    referer = REFERER
                )
            )
        }
    }
}
