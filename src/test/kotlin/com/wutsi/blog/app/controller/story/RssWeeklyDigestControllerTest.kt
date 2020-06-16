package com.wutsi.blog.app.controller.story

import com.rometools.rome.feed.rss.Channel
import com.wutsi.blog.SeleniumTestSupport
import org.junit.Assert.assertEquals
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
    }

}
