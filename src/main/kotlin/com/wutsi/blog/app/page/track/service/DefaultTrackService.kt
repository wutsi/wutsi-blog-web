package com.wutsi.blog.app.page.track.service

import com.wutsi.blog.app.backend.TrackBackend
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.track.model.PushTrackForm
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import com.wutsi.blog.client.track.PushTrackRequest
import com.wutsi.core.logging.KVLogger
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
@ConditionalOnProperty(
    value = ["wutsi.toggles.event-stream-tracking"],
    havingValue = "false"
)
class DefaultTrackService(
    private val backend: TrackBackend,
    private val request: HttpServletRequest,
    private val requestContext: RequestContext,
    private val logger: KVLogger
) : TrackService {
    override fun push(form: PushTrackForm): String {
        val request = createRequest(form)
        log(request)
        return backend.push(request).transactionId
    }

    private fun createRequest(form: PushTrackForm) = PushTrackRequest(
        time = System.currentTimeMillis(),
        value = form.value,
        long = form.long,
        lat = form.lat,
        page = form.page,
        ip = form.ip,
        event = form.event,
        pid = form.pid,
        uid = requestContext.currentUser()?.id?.toString(),
        referer = CookieHelper.get(CookieName.REFERER, request),
        ua = request.getHeader("User-Agent"),
        duid = CookieHelper.get(CookieName.DEVICE_UID, request),
        hid = form.hid,
        url = form.url
    )

    private fun log(request: PushTrackRequest) {
        logger.add("TrackEvent", request.event)
        logger.add("TrackPage", request.page)
        logger.add("TrackProductId", request.pid)
        logger.add("TrackUserId", request.uid)
        logger.add("TrackReferer", request.referer)
        logger.add("TrackIP", request.ip)
        logger.add("TrackLatitude", request.lat)
        logger.add("TrackLongitude", request.long)
    }
}
