package com.wutsi.blog.app.service.editorjs

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ImageFilter: Filter{
    override fun filter(html: Document) {
        var id = 0
        html.select("img").forEach { filter(++id, it) }
    }

    private fun filter(id: Int, img: Element) {
        img.attr("async-src", img.attr("src"))
        img.attr("data-lightbox","lightbox-$id")
        img.removeAttr("src")
    }
}
