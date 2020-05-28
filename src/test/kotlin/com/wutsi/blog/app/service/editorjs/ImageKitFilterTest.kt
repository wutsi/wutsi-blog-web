package com.wutsi.blog.app.service.editorjs

import com.wutsi.blog.app.service.ImageKitService
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ImageKitFilterTest {
    @Mock
    private lateinit var service: ImageKitService

    @Test
    fun filter() {
        val filter = ImageKitFilter(service)

        `when`(service.accept("foo.gif")).thenReturn(true)
        `when`(service.transform("foo.gif", "480px", null)).thenReturn("foo-480px.gif")
        `when`(service.transform("foo.gif", "800px", null)).thenReturn("foo-800px.gif")
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif'/>world</body>")
        filter.filter(doc)

        doc.select("img").forEach {
            assertEquals("foo-480px.gif 480w, foo-800px.gif 800w", it.attr("srcset"))
        }
    }

    @Test
    fun filterImageKitNotEnaled() {
        val filter = ImageKitFilter(service)

        `when`(service.accept("foo.gif")).thenReturn(false)
        val doc = Jsoup.parse("<body>Hello <img src='foo.gif'/>world</body>")
        filter.filter(doc)

        doc.select("img").forEach {
            assertFalse(it.hasAttr("srcset"))
        }
    }
}
