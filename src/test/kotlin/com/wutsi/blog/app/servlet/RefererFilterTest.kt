package com.wutsi.blog.app.servlet

import com.wutsi.core.logging.KVLogger
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import javax.servlet.FilterChain
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RunWith(MockitoJUnitRunner::class)
class RefererFilterTest {
    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var logger: KVLogger

    @Mock
    private lateinit var chain: FilterChain

    private lateinit var filter: RefererFilter

    @Before
    fun setUp() {
        filter = RefererFilter(logger, "https://www.wutsi.com")
    }

    @Test
    fun googleTraffic () {
        val referer = "https://www.google.com"
        `when`(request.getHeader("Referer")).thenReturn(referer)

        filter.doFilter(request, response, chain)

        verify(logger).add(RefererFilter.LOGGER_KEY, referer)

        val cookie = ArgumentCaptor.forClass(Cookie::class.java)
        verify(response).addCookie(cookie.capture())
        assertEquals(referer, cookie.value.value)
    }

    @Test
    fun directTraffic () {
        `when`(request.getHeader("Referer")).thenReturn("")

        filter.doFilter(request, response, chain)

        verify(logger).add(RefererFilter.LOGGER_KEY, RefererFilter.DIRECT)

        val cookie = ArgumentCaptor.forClass(Cookie::class.java)
        verify(response).addCookie(cookie.capture())
        assertEquals(RefererFilter.DIRECT, cookie.value.value)
    }

    @Test
    fun internalTraffic () {
        val referer = "https://www.wutsi.com"
        `when`(request.getHeader("Referer")).thenReturn(referer)

        filter.doFilter(request, response, chain)

        verify(logger).add(RefererFilter.LOGGER_KEY, referer)
        verify(response, never()).addCookie(ArgumentMatchers.any(Cookie::class.java))
    }
}
