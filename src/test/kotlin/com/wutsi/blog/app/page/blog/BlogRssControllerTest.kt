package com.wutsi.blog.app.page.blog

import com.rometools.rome.feed.rss.Channel
import com.wutsi.blog.SeleniumTestSupport
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate

class BlogRssControllerTest: SeleniumTestSupport() {
    val rest = RestTemplate()

    @Test
    fun `return weekly RSS digest for user`() {
        stub(HttpMethod.GET, "/v1/user/3", HttpStatus.OK, "v1/user/get-user3.json")
        stub(HttpMethod.GET, "/v1/user/@/roger.milla", HttpStatus.OK, "v1/user/get-user3.json")
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-user3.json")

        val response = rest.getForEntity("$url/@/roger.milla/rss", Channel::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val channel = response.body!!
        assertEquals("Roger Milla(@roger.milla) RSS Feed", channel.title)
        assertEquals("Just the best african soccer player ever!", channel.description)
        assertTrue(channel.link.endsWith("/@/roger.milla"))

        assertEquals(2, channel.items.size)

        assertEquals("Cameroon-Argentina", channel.items[0].title)
        assertEquals("Passe decisive: 1-0", channel.items[0].description.value)
        assertEquals("Roger Milla", channel.items[0].author)
        assertTrue(channel.items[0].link.endsWith("/read/20/cameroon-argentina"))

        assertEquals("Cameroon Rumania", channel.items[1].title)
        assertEquals("2 buts de Rogers", channel.items[1].description.value)
        assertEquals("Roger Milla", channel.items[1].author)
        assertTrue(channel.items[1].link.endsWith("/read/21/cameroon-rumania"))
    }
}
