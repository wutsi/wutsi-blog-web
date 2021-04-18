package com.wutsi.blog.app.page.track.model

data class PushTrackForm(
    val time: Long = System.currentTimeMillis(),
    val ip: String? = null,
    val lat: Double? = null,
    val long: Double? = null,
    val page: String? = null,
    val event: String? = null,
    val pid: String? = null,
    val value: String? = null,
    val hid: String? = null,
    val url: String? = null,
    val duid: String? = null,
    val ua: String? = null,
    val uid: String? = null,
    val referer: String? = null
)
