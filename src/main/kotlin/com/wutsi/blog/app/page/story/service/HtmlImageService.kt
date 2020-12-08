package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.common.service.ImageKitService
import org.springframework.stereotype.Service

@Service
class HtmlImageService(private val imageKit: ImageKitService) {
    fun sizes() = ""

    fun srcset(url: String): String {
        if (!imageKit.accept(url)) {
            return ""
        }

        return widths()
            .map { imageKit.transform(url, it) + " ${it}w" }
            .joinToString()
    }

    /* See https://getbootstrap.com/docs/4.0/layout/overview/ */
    private fun medias() = arrayListOf("320", "640", "1024")

    private fun widths() = arrayListOf("320", "640", "1024")
}
