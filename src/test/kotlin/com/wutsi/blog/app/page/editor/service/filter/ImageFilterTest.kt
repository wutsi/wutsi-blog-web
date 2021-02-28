package com.wutsi.blog.app.page.editor.service.filter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.common.service.ImageKitService
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.service.HtmlImageService
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ImageFilterTest {
    @Mock
    private lateinit var size: HtmlImageService

    @Mock
    private lateinit var imageKitService: ImageKitService

    @Mock
    private lateinit var requestContext: RequestContext

    private lateinit var filter: ImageFilter

    @Before
    fun setUp() {
        filter = ImageFilter(size, imageKitService, requestContext, 400)

        doReturn("foo-480px.gif 480w, foo-800px.gif 800w").whenever(size).srcset(any())
        doReturn("yo-man").whenever(size).sizes()
        doReturn(false).whenever(requestContext).isMobileUserAgent()
    }

    @Test
    fun `resize large image on mobile`() {
        doReturn(true).whenever(requestContext).isMobileUserAgent()
        doReturn("bar.gif").whenever(imageKitService).transform("foo.gif", "400")

        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='1024' height='200'/>world</body>")
        filter.filter(doc)

        assertEquals(
            "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  Hello \n" +
                "  <img src=\"bar.gif\" width=\"400\" loading=\"lazy\">world\n" +
                " </body>\n" +
                "</html>",
            doc.html()
        )
    }

    @Test
    fun `no not resize small image on mobile`() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='300' height='200'/>world</body>")
        filter.filter(doc)

        assertEquals(
            "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  Hello \n" +
                "  <img src=\"foo.gif\" width=\"300\" height=\"200\" loading=\"lazy\">world\n" +
                " </body>\n" +
                "</html>",
            doc.html()
        )
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
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='200' height='168'/>world</body>")
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
