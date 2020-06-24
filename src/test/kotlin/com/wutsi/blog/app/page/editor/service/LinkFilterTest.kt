package com.wutsi.blog.app.page.editor.service

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

class LinkFilterTest {
    private val filter = LinkFilter()

    @Test
    fun filter() {
        val doc = Jsoup.parse("<body>Hello <a href='foo.html'>world</a></body>")
        filter.filter(doc)

        doc.select("a").forEach {
            assertEquals("_new", it.attr("target"))
        }
    }


}
