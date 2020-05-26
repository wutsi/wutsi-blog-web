package com.wutsi.blog.app.service.editorjs

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageFilterTest {
    private val filter = ImageFilter()

    @Test
    fun async() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif'/>world</body>")
        filter.filter(doc)

        doc.select("a").forEach {
            assertFalse(it.hasAttr("src"))
            assertEquals("foo.gif", it.attr("data-src"))
            assertTrue("foo.gif", it.hasClass("lozad"))
        }
    }
}
