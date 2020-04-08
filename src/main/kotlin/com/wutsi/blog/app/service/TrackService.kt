package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.TrackBackend
import com.wutsi.blog.app.model.PushTrackForm
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.CookieName
import com.wutsi.blog.client.track.PushTrackRequest
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest


@Service
class TrackService(
        private val backend: TrackBackend,
        private val request: HttpServletRequest,
        private val requestContext: RequestContext
) {
    fun push(form: PushTrackForm): String {
        val request = createRequest(form)
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
            uid = requestContext.currentUser()?.id.toString(),
            referer = CookieHelper.get(CookieName.REFERER, request),
            ua = request.getHeader("User-Agent"),
            duid = CookieHelper.get(CookieName.DEVICE_UID, request)
    )
}
