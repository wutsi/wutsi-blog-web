package com.wutsi.blog.app.page.track.service

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.track.model.PushTrackForm
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import com.wutsi.core.logging.KVLogger
import com.wutsi.stream.EventStream
import com.wutsi.tracking.dto.PushTrackRequest
import com.wutsi.tracking.event.TrackSubmittedEventPayload
import com.wutsi.tracking.event.TrackingEventType
import org.springframework.stereotype.Service

@Service
class EventStreamTrackService(
    private val eventStream: EventStream,
    private val requestContext: RequestContext,
    private val logger: KVLogger
) : TrackService {
    override fun push(form: PushTrackForm): String {
        logger.add("Implementation", "EventStreamTrackService")
        eventStream.publish(
            type = TrackingEventType.TRACK_SUBMITTED.urn,
            payload = createPayload(form)
        )
        return ""
    }

    private fun createPayload(form: PushTrackForm) = TrackSubmittedEventPayload(
        request = createRequest(form)
    )

    fun createRequest(form: PushTrackForm) = PushTrackRequest(
        time = System.currentTimeMillis(),
        value = form.value,
        long = form.long,
        lat = form.lat,
        page = form.page,
        ip = form.ip,
        event = form.event,
        pid = form.pid,
        uid = requestContext.currentUser()?.id?.toString(),
        referer = CookieHelper.get(CookieName.REFERER, requestContext.request),
        ua = requestContext.request.getHeader("User-Agent"),
        duid = CookieHelper.get(CookieName.DEVICE_UID, requestContext.request),
        hid = form.hid,
        url = form.url
    )
}
