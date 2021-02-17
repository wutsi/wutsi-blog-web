package com.wutsi.blog.app.common.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ImageKitService(
    @Value("\${wutsi.toggles.image-kit}") private val enabled: Boolean,
    @Value("\${wutsi.image-kit.origin-url}") private val originUrl: String,
    @Value("\${wutsi.image-kit.endpoint-url}") private val endpoint: String
) {
    fun accept(url: String?) = enabled && url?.startsWith(originUrl) == true

    fun transform(url: String?, width: String? = null, height: String? = null): String? {
        if (!accept(url)) {
            return url
        }

        val xurl = endpoint + url?.substring(originUrl.length)
        val i = xurl.lastIndexOf('/')
        val prefix = xurl.substring(0, i)
        val suffix = xurl.substring(i)
        val tr = transformations(width, height)
        return prefix + tr + suffix
    }

    private fun transformations(width: String? = null, height: String? = null, autoFocus: Boolean = true): String {
        if (width == null && height == null) {
            return ""
        }
        val sb = StringBuilder()
        if (width != null) {
            sb.append("w-$width")
        }
        if (height != null) {
            if (!sb.isEmpty()) {
                sb.append(",")
            }
            sb.append("h-$height")
        }
        if (autoFocus) {
            if (!sb.isEmpty()) {
                sb.append(",")
            }
            sb.append("fo-auto")
        }
        return "/tr:" + sb.toString()
    }
}
