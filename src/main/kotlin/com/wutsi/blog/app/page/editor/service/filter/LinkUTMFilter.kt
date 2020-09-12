package com.wutsi.blog.app.page.editor.service.filter

import com.wutsi.blog.app.page.editor.service.Filter
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class LinkUTMFilter: Filter {
    override fun filter(html: Document) {
        html.select("a").forEach { filter(it) }
    }

    private fun filter(link: Element) {
        val href = link.attr("href")
        var param = "utm_source=wutsi"
        if (href.indexOf("?") > 0) {
            link.attr("href", "$href&$param")
        } else {
            link.attr("href", "$href?$param")
        }
    }
}
