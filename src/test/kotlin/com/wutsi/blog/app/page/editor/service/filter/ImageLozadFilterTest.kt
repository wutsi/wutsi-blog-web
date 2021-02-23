package com.wutsi.blog.app.page.editor.service.filter

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.common.service.Toggles
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ImageLozadFilterTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @Test
    fun `will filter when toggle is on`() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' srcset='foo-200.gif 200w, bar-300.gif 300w'/>world</body>")

        val filter = createImageLozadFilter(true)
        filter.filter(doc)

        doc.select("img").forEach {
            assertTrue(it.hasClass("lozad"))
            assertFalse(it.hasAttr("srcset"))
            assertFalse(it.hasAttr("src"))

            assertEquals("foo.gif", it.attr("data-src"))
            assertEquals("foo-200.gif 200w, bar-300.gif 300w", it.attr("data-srcset"))
        }
    }

    @Test
    fun `will not filter when toggle is off`() {
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif' srcset='foo-200.gif 200w, bar-300.gif 300w'/>world</body>")

        val filter = createImageLozadFilter(false)
        filter.filter(doc)

        assertEquals(
            "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  Hello \n" +
                "  <img src=\"foo.gif\" srcset=\"foo-200.gif 200w, bar-300.gif 300w\">world\n" +
                " </body>\n" +
                "</html>",
            doc.html()
        )
    }

    private fun createImageLozadFilter(enabled: Boolean): ImageLozadFilter {
        val toggles = Toggles()
        toggles.lozad = enabled

        doReturn(toggles).whenever(requestContext).toggles()

        return ImageLozadFilter(requestContext)
    }
}
