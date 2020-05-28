package com.wutsi.blog.app.service.editorjs

import com.wutsi.blog.app.service.HtmlImageService
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ImageKitFilter(
        private val sizes: HtmlImageService
): Filter {
    override fun filter(html: Document) {
        html.select("img")
                .forEach { filter(it) }
    }

    private fun filter(img: Element) {
        val url = img.attr("src")
        val srcset = sizes.srcset(url)
        if (srcset.isNotEmpty()) {
            img.attr("srcset", srcset)
            img.attr("sizes", sizes.sizes())
        }
    }
}
