package com.wutsi.blog.app.service.editorjs

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ImageFilter: Filter{
    override fun filter(html: Document) {
        html.select("img").forEach { filter(it) }
    }

    private fun filter(img: Element) {
        val src = img.attr("src")
        if (src.isNotEmpty()) {
            img.addClass("lozad")
            img.attr("data-src", img.attr("src"))
            img.removeAttr("src")
        }
    }
}
