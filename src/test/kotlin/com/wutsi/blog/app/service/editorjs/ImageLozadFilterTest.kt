package com.wutsi.blog.app.service.editorjs

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageLozadFilterTest {
    private val filter = ImageLozadFilter()

    @Test
    fun filter() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' srcset='foo-200.gif 200w, bar-300.gif 300w'/>world</body>")
        filter.filter(doc)

        doc.select("img").forEach {
            assertTrue(it.hasClass("lozad"))
            assertFalse(it.hasAttr("srcset"))
            assertFalse(it.hasAttr("src"))

            assertEquals("foo.gif", it.attr("data-src"))
            assertEquals("foo-200.gif 200w, bar-300.gif 300w", it.attr("data-srcset"))
        }
    }
}
