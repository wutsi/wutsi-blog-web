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
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.net.HttpURLConnection
import java.net.URL
import java.time.Clock

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
class StoryMailPixelControllerTest {
    @MockBean
    private lateinit var trackService: TrackService

    @LocalServerPort
    private lateinit var port: Integer

    @MockBean
    private lateinit var clock: Clock

    private val time = 100000L

    @BeforeEach
    fun setUp() {
        doReturn(time).whenever(clock).millis()
    }

    @Test
    fun `pixel is returned`() {
        val cnn = URL("http://localhost:$port/story/pixel/1.png").openConnection()

        val headers = cnn.headerFields
        assertEquals("image/png", cnn.contentType)
        assertEquals(4066, cnn.contentLength)
        assertEquals(true, headers["Pragma"]?.contains("no-cache"))
        assertEquals(true, headers["Cache-Control"]?.contains("no-cache, max-age=0"))
    }

    @Test
    fun `pixel is tracked`() {
        val cnn = URL(
            "http://localhost:$port/story/pixel/1.png?u=555&d=1"
        ).openConnection() as HttpURLConnection
        cnn.setRequestProperty("User-Agent", "test")
        cnn.headerFields // Will download content

        val request = argumentCaptor<PushTrackForm>()
        verify(trackService, times(2)).push(request.capture())

        assertEquals(request.firstValue.hid, request.firstValue.hid)
        assertEquals("1", request.firstValue.pid)
        assertEquals(request.firstValue.duid, request.firstValue.duid)
        assertEquals("test", request.firstValue.ua)
        assertEquals(PageName.READ, request.firstValue.page)
        assertEquals("555", request.firstValue.uid)
        assertEquals("readstart", request.firstValue.event)
        assertEquals(time, request.firstValue.time)
        assertEquals("http://localhost:$port/story/pixel/1.png?u=555&d=1", request.firstValue.url)

        assertEquals(request.firstValue.hid, request.secondValue.hid)
        assertEquals("1", request.secondValue.pid)
        assertEquals(request.firstValue.duid, request.secondValue.duid)
        assertEquals("test", request.secondValue.ua)
        assertEquals(PageName.READ, request.firstValue.page)
        assertEquals("555", request.secondValue.uid)
        assertEquals("scroll", request.secondValue.event)
        assertEquals("100", request.secondValue.value)
        assertEquals(time + 1 * 60 * 1000, request.secondValue.time)
        assertEquals("http://localhost:$port/story/pixel/1.png?u=555&d=1", request.secondValue.url)
    }

    @Test
    fun `pixel without duration is tracked`() {
        val cnn = URL(
            "http://localhost:$port/story/pixel/1.png?u=555"
        ).openConnection() as HttpURLConnection
        cnn.setRequestProperty("User-Agent", "test")
        cnn.headerFields // Will download content

        val request = argumentCaptor<PushTrackForm>()
        verify(trackService).push(request.capture())

        assertEquals(request.firstValue.hid, request.firstValue.hid)
        assertEquals("1", request.firstValue.pid)
        assertEquals(request.firstValue.duid, request.firstValue.duid)
        assertEquals("test", request.firstValue.ua)
        assertEquals(PageName.READ, request.firstValue.page)
        assertEquals("555", request.firstValue.uid)
        assertEquals("readstart", request.firstValue.event)
        assertEquals(time, request.firstValue.time)
        assertEquals("http://localhost:$port/story/pixel/1.png?u=555", request.firstValue.url)
    }
}
