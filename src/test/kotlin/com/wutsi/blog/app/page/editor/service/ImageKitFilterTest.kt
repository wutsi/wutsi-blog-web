package com.wutsi.blog.app.page.editor.service

import com.wutsi.blog.app.service.HtmlImageService
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ImageKitFilterTest {
    @Mock
    private lateinit var size: HtmlImageService

    @InjectMocks
    private lateinit var filter: ImageKitFilter

    @Test
    fun filter() {
        `when`(size.srcset("foo.gif")).thenReturn("foo-480px.gif 480w, foo-800px.gif 800w")
        `when`(size.sizes()).thenReturn("yo-man")

        val doc = Jsoup.parse("<body>Hello <img src='foo.gif'/>world</body>")
        filter.filter(doc)

        doc.select("img").forEach {
            assertEquals("foo-480px.gif 480w, foo-800px.gif 800w", it.attr("srcset"))
            assertEquals("yo-man", it.attr("sizes"))
        }
    }

    @Test
    fun filterNoSource() {
        `when`(size.srcset("foo.gif")).thenReturn("")
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif'/>world</body>")
        filter.filter(doc)

        doc.select("img").forEach {
            assertFalse(it.hasAttr("srcset"))
            assertFalse(it.hasAttr("sizes"))
        }
    }
}
