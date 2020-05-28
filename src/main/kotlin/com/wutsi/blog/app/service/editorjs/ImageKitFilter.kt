package com.wutsi.blog.app.service.editorjs

import com.wutsi.blog.app.service.ImageKitService
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ImageKitFilter(private val service: ImageKitService): Filter{
    override fun filter(html: Document) {
        html.select("img")
                .forEach { filter(it) }
    }

    private fun filter(img: Element) {
        val url = img.attr("src")
        if (!service.accept(url)) {
            return
        }

        val src480 = service.transform(url, "480px")
        val src800 = service.transform(url, "800px")
        img.attr("srcset", "$src480 480w, $src800 800w")
    }
}
