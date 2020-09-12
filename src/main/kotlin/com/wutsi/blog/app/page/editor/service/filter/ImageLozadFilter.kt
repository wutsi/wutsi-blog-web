package com.wutsi.blog.app.page.editor.service.filter

import com.wutsi.blog.app.page.editor.service.Filter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ImageLozadFilter: Filter {
    override fun filter(html: Document) {
        html.select("img").forEach { filter(it) }
    }

    private fun filter(img: Element) {
        img.addClass("lozad")
        transform(img, "src")
        transform(img, "srcset")
    }

    private fun transform(img: Element, attr: String){
        val value = img.attr(attr)
        if (value.isNotEmpty()) {
            img.attr("data-$attr", value)
            img.removeAttr(attr)
        }
    }
}
