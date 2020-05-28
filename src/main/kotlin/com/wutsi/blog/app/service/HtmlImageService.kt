package com.wutsi.blog.app.service

import org.springframework.stereotype.Service

@Service
class HtmlImageService(private val imageKit: ImageKitService) {
    fun sizes(): String {
        val buff = StringBuffer()
        val medias = medias()
        val widths = widths()

        var i = 0
        medias.forEach({
            val width = widths[i]
            val maxWidth = medias[i]

            if (!buff.isEmpty()){
                buff.append(",")
            }
            if (i == medias.size-1){
                buff.append("${width}px")
            } else {
                buff.append("(max-width: ${maxWidth}px) ${width}px")
            }

            i++
        })
        return buff.toString()
    }

    fun srcset(url: String): String {
        if (!imageKit.accept(url)) {
            return ""
        }

        return widths()
                .map { imageKit.transform(url, it) + " ${it}w" }
                .joinToString ()

    }

    /* See https://getbootstrap.com/docs/4.0/layout/overview/ */
    private fun medias() = arrayListOf("576", "768", "992")

    private fun widths() = arrayListOf("576", "768", "992")
}

