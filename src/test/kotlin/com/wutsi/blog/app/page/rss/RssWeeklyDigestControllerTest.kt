package com.wutsi.blog.app.page.rss

import com.rometools.rome.feed.rss.Channel
import com.wutsi.blog.SeleniumTestSupport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate

class RssWeeklyDigestControllerTest: SeleniumTestSupport() {
    val rest = RestTemplate()

    @Test
    fun `return weekly RSS digest`() {
        val response = rest.getForEntity("$url/rss/digest/weekly", Channel::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val items = response.body?.items!!
        assertEquals(7, items.size)

        val item = items[0]
        assertEquals("Lorem Ipsum", item.title)
        assertEquals("Ray Sponsible", item.author)
        assertEquals("http://localhost:8081/read/20/lorem-ipsum", item.link)
        assertEquals("Lorem Ipsum is simply dummy text of the printing and typesetting industry", item.description.value)
        assertNotNull(item.pubDate)

    }

    @Test
    fun `return weekly RSS digest for user`() {
        stub(HttpMethod.GET, "/v1/user/3", HttpStatus.OK, "v1/user/get-user3.json")
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-user3.json")

        val response = rest.getForEntity("$url/rss/digest/weekly?userId=3", Channel::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val items = response.body?.items!!
        assertEquals(2, items.size)

    }
}
