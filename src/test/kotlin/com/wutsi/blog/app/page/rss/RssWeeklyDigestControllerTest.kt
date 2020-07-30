package com.wutsi.blog.app.page.rss

import com.rometools.rome.feed.rss.Channel
import com.wutsi.blog.SeleniumTestSupport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate

class RssWeeklyDigestControllerTest: SeleniumTestSupport() {
    val rest = RestTemplate()

    @Test
    fun index() {
        val response = rest.getForEntity("$url/rss/digest/weekly", Channel::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val items = response.body.items
        assertEquals(7, items.size)

        val item = items[0]
        assertEquals("Lorem Ipsum", item.title)
        assertEquals("Ray Sponsible", item.author)
        assertEquals("http://localhost:8081/read/20/lorem-ipsum", item.link)
        assertEquals("Lorem Ipsum is simply dummy text of the printing and typesetting industry", item.description.value)
        assertNotNull(item.pubDate)

    }

}
