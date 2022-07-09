package com.wutsi.blog.app.page.editor.service.filter

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.common.service.ImageKitService
import com.wutsi.blog.app.common.service.RequestContext
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ImageFilterTest {
    @Mock
    private lateinit var imageKitService: ImageKitService

    @Mock
    private lateinit var requestContext: RequestContext

    private lateinit var filter: ImageFilter

    @BeforeEach
    fun setUp() {
        filter = ImageFilter(imageKitService, requestContext, 960, 400)
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
    fun `resize image with large width on desktop`() {
        doReturn("bar.gif").whenever(imageKitService).transform("foo.gif", "960")

        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='1024' height='200'/>world</body>")
        filter.filter(doc)

        assertEquals(
            "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  Hello \n" +
                "  <img src=\"bar.gif\" width=\"960\" loading=\"lazy\">world\n" +
                " </body>\n" +
                "</html>",
            doc.html()
        )
    }

    @Test
    fun `no not resize small image on desktop`() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' width='200' height='168'/>world</body>")
        filter.filter(doc)

        assertEquals(
            "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  Hello \n" +
                "  <img src=\"foo.gif\" width=\"200\" height=\"168\" loading=\"lazy\">world\n" +
                " </body>\n" +
                "</html>",
            doc.html()
        )
    }
}
