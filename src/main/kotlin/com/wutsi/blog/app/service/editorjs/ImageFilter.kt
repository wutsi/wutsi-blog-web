package com.wutsi.blog.app.service.editorjs

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ImageFilter: Filter{
    override fun filter(html: Document) {
        html.select("img").forEach { filter(it) }
    }

    private fun filter(img: Element) {
        img.attr("async-src", img.attr("src"))
        img.removeAttr("src")
    }
}
