package com.wutsi.blog.app.page.editor.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.story.service.HtmlImageService
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ImageKitFilterTest {
    @Mock
    private lateinit var size: HtmlImageService

    @InjectMocks
    private lateinit var filter: ImageKitFilter

    @Before
    fun setUp() {
        doReturn("foo-480px.gif 480w, foo-800px.gif 800w").whenever(size).srcset(any())
        doReturn("yo-man").whenever(size).sizes()
    }

    @Test
    fun filterLargeWidth() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='1024' height='200'/>world</body>")
        filter.filter(doc)

        doc.select("img").forEach {
            assertEquals("foo-480px.gif 480w, foo-800px.gif 800w", it.attr("srcset"))
            assertEquals("yo-man", it.attr("sizes"))
        }
    }

    @Test
    fun filterLargeHeight() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='200' height='1024'/>world</body>")
        filter.filter(doc)

        doc.select("img").forEach {
            assertEquals("foo-480px.gif 480w, foo-800px.gif 800w", it.attr("srcset"))
            assertEquals("yo-man", it.attr("sizes"))
        }
    }

    @Test
    fun filterSmallImage() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='200' heigh='168'/>world</body>")
        filter.filter(doc)

        doc.select("img").forEach {
            assertFalse(it.hasAttr("srcset"))
            assertFalse(it.hasAttr("sizes"))
        }
    }

    @Test
    fun filterNoSource() {
        doReturn("").whenever(size).srcset(any())

        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='1200' height='1024'/>world</body>")
        filter.filter(doc)

        doc.select("img").forEach {
            assertFalse(it.hasAttr("srcset"))
            assertFalse(it.hasAttr("sizes"))
        }
    }
}
