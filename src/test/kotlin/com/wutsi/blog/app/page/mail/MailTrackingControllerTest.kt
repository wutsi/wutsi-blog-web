package com.wutsi.blog.app.page.mail

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.track.model.PushTrackForm
import com.wutsi.blog.app.page.track.service.TrackService
import com.wutsi.blog.app.util.PageName
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.Clock

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
class MailTrackingControllerTest {
    @MockBean
    private lateinit var trackService: TrackService

    @LocalServerPort
    private lateinit var port: Integer

    @MockBean
    private lateinit var clock: Clock

    private val time = 100000L

    @Before
    fun setUp() {
        doReturn(time).whenever(clock).millis()
    }

    @Test
    fun `pixel is returned and no cached`() {
        val cnn = URL("http://localhost:$port/mail/track/1.png").openConnection()

        val headers = cnn.headerFields
        assertEquals("image/png", cnn.contentType)
        assertEquals(4066, cnn.contentLength)
        assertEquals(true, headers["Pragma"]?.contains("no-cache"))
        assertEquals(true, headers["Cache-Control"]?.contains("no-cache, max-age=0"))
    }

    @Test
    fun `read is tracked`() {
        val cnn = URL(
            "http://localhost:$port/mail/track/1.png?u=555&d=1&hid=1111&did=2222"
        ).openConnection() as HttpURLConnection
        cnn.setRequestProperty("User-Agent", "test")
        cnn.headerFields // Will download content

        val request = argumentCaptor<PushTrackForm>()
        verify(trackService, times(3)).push(request.capture())

        assertEquals("1111", request.firstValue.hid)
        assertEquals("1", request.firstValue.pid)
        assertEquals("2222", request.firstValue.duid)
        assertEquals("test", request.firstValue.ua)
        assertEquals(PageName.READ, request.firstValue.page)
        assertEquals("555", request.firstValue.uid)
        assertEquals("click", request.firstValue.event)
        assertEquals(time, request.firstValue.time)
        assertEquals("http://localhost:$port/mail/track/1.png?u=555&d=1&hid=1111&did=2222", request.firstValue.url)

        assertEquals("1111", request.secondValue.hid)
        assertEquals("1", request.secondValue.pid)
        assertEquals("2222", request.secondValue.duid)
        assertEquals("test", request.secondValue.ua)
        assertEquals(PageName.READ, request.secondValue.page)
        assertEquals("555", request.secondValue.uid)
        assertEquals("readstart", request.secondValue.event)
        assertEquals(time, request.secondValue.time)
        assertEquals("http://localhost:$port/mail/track/1.png?u=555&d=1&hid=1111&did=2222", request.secondValue.url)

        assertEquals("1111", request.thirdValue.hid)
        assertEquals("1", request.thirdValue.pid)
        assertEquals("2222", request.thirdValue.duid)
        assertEquals("test", request.thirdValue.ua)
        assertEquals(PageName.READ, request.thirdValue.page)
        assertEquals("555", request.thirdValue.uid)
        assertEquals("scroll", request.thirdValue.event)
        assertEquals("100", request.thirdValue.value)
        assertEquals(time + 1 * 60 * 1000, request.thirdValue.time)
        assertEquals("http://localhost:$port/mail/track/1.png?u=555&d=1&hid=1111&did=2222", request.thirdValue.url)
    }

    @Test
    fun `read without duration is tracked`() {
        val cnn = URL(
            "http://localhost:$port/mail/track/1.png?u=555&hid=1111&did=2222"
        ).openConnection() as HttpURLConnection
        cnn.setRequestProperty("User-Agent", "test")
        cnn.headerFields // Will download content

        val request = argumentCaptor<PushTrackForm>()
        verify(trackService, times(2)).push(request.capture())

        assertEquals("1111", request.firstValue.hid)
        assertEquals("1", request.firstValue.pid)
        assertEquals("2222", request.firstValue.duid)
        assertEquals("test", request.firstValue.ua)
        assertEquals(PageName.READ, request.firstValue.page)
        assertEquals("555", request.firstValue.uid)
        assertEquals("click", request.firstValue.event)
        assertEquals(time, request.firstValue.time)
        assertEquals("http://localhost:$port/mail/track/1.png?u=555&hid=1111&did=2222", request.firstValue.url)

        assertEquals("1111", request.secondValue.hid)
        assertEquals("1", request.secondValue.pid)
        assertEquals("2222", request.secondValue.duid)
        assertEquals("test", request.secondValue.ua)
        assertEquals(PageName.READ, request.secondValue.page)
        assertEquals("555", request.secondValue.uid)
        assertEquals("readstart", request.secondValue.event)
        assertEquals(time, request.secondValue.time)
        assertEquals("http://localhost:$port/mail/track/1.png?u=555&hid=1111&did=2222", request.secondValue.url)
    }

    @Test
    fun `link click is tracked`() {
        val url = "http://localhost:$port/mail/track/link?u=555&hid=1111&did=2222&url=" + URLEncoder.encode("http://www.google.com", "utf-8")
        val cnn = URL(url).openConnection() as HttpURLConnection
        cnn.instanceFollowRedirects = false
        val headers = cnn.headerFields // Will download content

        assertEquals(302, cnn.responseCode)
        assertEquals(true, headers["Location"]?.contains("http://www.google.com"))

        val request = argumentCaptor<PushTrackForm>()
        verify(trackService).push(request.capture())

        assertEquals("1111", request.firstValue.hid)
//        assertEquals("1", request.firstValue.pid)
        assertEquals("2222", request.firstValue.duid)
        assertEquals("Java/11.0.8", request.firstValue.ua)
        assertEquals(PageName.READ, request.firstValue.page)
        assertEquals("555", request.firstValue.uid)
        assertEquals("link", request.firstValue.event)
        assertEquals(time, request.firstValue.time)
        assertEquals("http://www.google.com", request.firstValue.url)
    }
}
