package com.wutsi.blog.app.service.editorjs

import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test

class ImageFilterTest {
    private val filter = ImageFilter()

    @Test
    fun filter() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif'/>world</body>")
        filter.filter(doc)

        doc.select("a").forEach {
            Assert.assertFalse(it.hasAttr("src"))
            Assert.assertEquals("foo.gif", it.attr("async-src"))
        }
    }

}
