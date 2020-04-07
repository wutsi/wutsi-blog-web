package com.wutsi.blog.app.service.editorjs

import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class EJSFilterSet(
        private val filters: List<Filter> = arrayListOf(
                ImageFilter(),
                LinkFilter()
        )
): Filter{

    override fun filter(html: Document) {
        filters.forEach{ it.filter(html) }
    }
}
