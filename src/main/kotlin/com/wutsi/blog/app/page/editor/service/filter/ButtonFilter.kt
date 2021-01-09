package com.wutsi.blog.app.page.editor.service.filter

import com.wutsi.blog.app.page.editor.service.Filter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ButtonFilter : Filter {
    override fun filter(html: Document) {
        html.select("div.button a").forEach { filter(it) }
    }

    private fun filter(img: Element) {
        img.addClass("btn")
        img.addClass("btn-primary")
    }

    private fun transform(img: Element, attr: String) {
        val value = img.attr(attr)
        if (value.isNotEmpty()) {
            img.attr("data-$attr", value)
            img.removeAttr(attr)
        }
    }
}
