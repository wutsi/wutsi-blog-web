package com.wutsi.blog.app.service.editorjs

import org.jsoup.nodes.Document

interface Filter{
    fun filter(html: Document)
}
