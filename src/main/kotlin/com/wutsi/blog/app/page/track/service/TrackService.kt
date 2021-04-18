package com.wutsi.blog.app.page.track.service

import com.wutsi.blog.app.page.track.model.PushTrackForm

interface TrackService {
    fun push(form: PushTrackForm): String
}
