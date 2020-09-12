package com.wutsi.blog.app.page.editor.service.filter

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

class LinkUTMFilterTest {
    private val filter = LinkUTMFilter()

    @Test
    fun filter1() {
        val doc = Jsoup.parse("<body>Hello <a href='foo.html'>world</a></body>")
        filter.filter(doc)

        doc.select("a").forEach {
            assertEquals("foo.html?utm_source=wutsi", it.attr("href"))
        }
    }

    @Test
    fun filter2() {
        val doc = Jsoup.parse("<body>Hello <a href='foo.html?x=1'>world</a></body>")
        filter.filter(doc)

        doc.select("a").forEach {
            assertEquals("foo.html?x=1&utm_source=wutsi", it.attr("href"))
        }
    }

}
