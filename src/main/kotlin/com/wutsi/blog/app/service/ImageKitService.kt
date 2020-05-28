package com.wutsi.blog.app.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ImageKitService(
        @Value("\${wutsi.toggles.imagekit}") private val enabled: Boolean,
        @Value("\${wutsi.imagekit.origin-url}") private val originUrl: String,
        @Value("\${wutsi.imagekit.endpoint-url}") private val endpoint: String
) {
    fun accept(url: String) = enabled && url.startsWith(originUrl)

    fun transform(url: String, width: String?=null, height: String?=null): String {
        if (accept(url)){
            return url
        }

        val xurl = endpoint + url.substring(originUrl.length)
        val i = xurl.lastIndexOf('/')
        val prefix = xurl.substring(0, i)
        val suffix = xurl.substring(i);
        val tr = transformations(width, height)
        return prefix + tr + suffix
    }

    private fun transformations(width: String?=null, height: String?=null): String {
        if (width == null && height == null) {
            return ""
        }
        val sb = StringBuilder()
        sb.append("/tr:")
        if (width != null){
            sb.append("w-$width")
        }
        if (height != null){
            if (width != null){
                sb.append(",")
            }
            sb.append("h-$height")
        }
        return sb.toString()
    }
}

