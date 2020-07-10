package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.common.service.ImageKitService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HtmlImageServiceTest {
    @Mock
    lateinit var imageKit: ImageKitService

    @InjectMocks
    lateinit var service: HtmlImageService

    @Test
    fun sizes() {
        val value  = service.sizes()
        assertEquals("", value)
    }

    @Test
    fun srcset() {
        val url = "http://foo.com/1.png"
        `when`(imageKit.accept(url)).thenReturn(true)
        `when`(imageKit.transform(url, "320")).thenReturn("http://foo.com/576/1.png")
        `when`(imageKit.transform(url, "640")).thenReturn("http://foo.com/768/1.png")
        `when`(imageKit.transform(url, "1024")).thenReturn("http://foo.com/992/1.png")

        val value  = service.srcset(url)
        assertEquals("http://foo.com/576/1.png 320w, http://foo.com/768/1.png 640w, http://foo.com/992/1.png 1024w", value)
    }
}
