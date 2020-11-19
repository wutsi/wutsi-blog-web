package com.wutsi.blog.app.page.home

import com.rometools.rome.feed.rss.Channel
import com.wutsi.blog.SeleniumTestSupport
import org.junit.Assert.*
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate

class HomeRssControllerTest: SeleniumTestSupport() {
    val rest = RestTemplate()

    @Test
    fun `return weekly RSS digest`() {
        val response = rest.getForEntity("$url/rss", Channel::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val channel = response.body!!
        assertEquals("Wutsi RSS Feed", channel.title)
        assertEquals("Wutsi RSS Feed", channel.description)

        assertEquals(7, channel.items.size)
    }
}
