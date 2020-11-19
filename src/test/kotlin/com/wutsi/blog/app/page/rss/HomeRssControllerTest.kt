package com.wutsi.blog.app.page.rss

import com.rometools.rome.feed.rss.Channel
import com.wutsi.blog.SeleniumTestSupport
import org.junit.Assert.*
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate

class HomeRssControllerTest: SeleniumTestSupport() {
    val rest = RestTemplate()

    @Test
    fun `return site RSS`() {
        val response = rest.getForEntity("$url/rss", Channel::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        validateRSS(response.body!!)
    }


    @Test
    fun `return site RSS for date range`() {
        val response = rest.getForEntity("$url/rss?startDate=2020-12-01&endDate=2020-12-10", Channel::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        validateRSS(response.body!!)
    }

    private fun validateRSS(channel: Channel){
        assertEquals("Wutsi RSS Feed", channel.title)
        assertEquals("Wutsi RSS Feed", channel.description)

        assertEquals(7, channel.items.size)
        val item = channel.items[0]
        assertEquals("Lorem Ipsum", item.title)
        assertEquals("Ray Sponsible", item.author)
        assertEquals("http://localhost:8081/read/20/lorem-ipsum", item.link)
        assertEquals("Lorem Ipsum is simply dummy text of the printing and typesetting industry", item.description.value)
        assertNotNull(item.pubDate)
        assertEquals(1, channel.items[0].enclosures.size)
        assertEquals("image/jpeg", channel.items[0].enclosures[0].type)
        assertEquals("https://images.pexels.com/photos/2167395/pexels-photo-2167395.jpeg", channel.items[0].enclosures[0].url)
    }
}
