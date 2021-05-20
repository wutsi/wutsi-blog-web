package com.wutsi.blog.app.page.story

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.client.follower.FollowerDto
import com.wutsi.blog.client.follower.SearchFollowerResponse
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class ReadSubscriberControllerTest : SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")
    }

    @Test
    fun `story with SUSCRIBER access are viewed partially by anonymous users`() {
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99-access-subscriber.json")

        driver.get("$url/read/20/test")

        assertElementCount("#story-paywall", 1)
    }

    @Test
    fun `story with SUSCRIBER access are viewed partially by non subscriber`() {
        login()
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99-access-subscriber.json")

        driver.get("$url/read/20/test")

        assertElementCount("#story-paywall", 1)
    }

    @Test
    fun `story with SUSCRIBER access are viewed completely by subscriber`() {
        login()
        doReturn(SearchFollowerResponse(followers = listOf(FollowerDto()))).whenever(followerApi).search(any())
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99-access-subscriber.json")

        driver.get("$url/read/20/test")

        assertElementCount("#story-paywall", 0)
    }
}
