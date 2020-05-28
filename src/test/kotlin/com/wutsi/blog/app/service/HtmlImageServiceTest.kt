package com.wutsi.blog.app.service

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
        assertEquals("(max-width: 576px) 576px,(max-width: 768px) 768px,992px", value)
    }

    @Test
    fun srcset() {
        val url = "http://foo.com/1.png"
        `when`(imageKit.accept(url)).thenReturn(true)
        `when`(imageKit.transform(url, "576")).thenReturn("http://foo.com/576/1.png")
        `when`(imageKit.transform(url, "768")).thenReturn("http://foo.com/768/1.png")
        `when`(imageKit.transform(url, "992")).thenReturn("http://foo.com/992/1.png")

        val value  = service.srcset(url)
        assertEquals("http://foo.com/576/1.png 576w, http://foo.com/768/1.png 768w, http://foo.com/992/1.png 992w", value)
    }
}
