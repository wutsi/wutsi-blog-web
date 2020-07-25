package com.wutsi.blog.app.page.editor.service

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class LinkTargetFilter: Filter{
    override fun filter(html: Document) {
        html.select("a").forEach { filter(it) }
    }

    private fun filter(img: Element) {
        img.attr("target", "_new")
    }
}
