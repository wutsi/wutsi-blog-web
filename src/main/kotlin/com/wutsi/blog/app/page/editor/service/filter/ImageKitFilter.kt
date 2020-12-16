package com.wutsi.blog.app.page.editor.service.filter

import com.wutsi.blog.app.page.editor.service.Filter
import com.wutsi.blog.app.page.story.service.HtmlImageService
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ImageKitFilter(
    private val sizes: HtmlImageService
) : Filter {
    override fun filter(html: Document) {
        html.select("img")
            .forEach { filter(it) }
    }

    private fun filter(img: Element) {
        val url = img.attr("src")
        if (isLargeImage(img)) {
            val srcset = sizes.srcset(url)
            if (srcset.isNotEmpty()) {
                img.attr("srcset", srcset)
                img.attr("sizes", sizes.sizes())
            }
        }
    }

    private fun isLargeImage(img: Element): Boolean {
        try {
            val width = img.attr("width").toInt()
            val height = img.attr("height").toInt()
            return width > 960 || height > 960
        } catch (ex: Exception) {
            return false
        }
    }
}
