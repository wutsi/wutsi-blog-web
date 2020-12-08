package com.wutsi.blog.app.page.pwa.model

data class ManifestModel(
    val name: String,
    val short_name: String,
    val start_url: String,
    val display: String,
    val background_color: String,
    val theme_color: String,
    val orientation: String,
    val icons: List<IconModel>,
    val gcm_sender_id: String
)
