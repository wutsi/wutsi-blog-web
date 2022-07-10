package com.wutsi.blog.app.page.follower

import com.wutsi.blog.SeleniumMobileTestSupport
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class WhoToFollowControllerTest : SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
    }

    @Test
    fun `blog page`() {
        driver.get("$url/@/ray.sponsible")
        verifyWhoToFollow()
    }

    @Test
    fun `story reader`() {
        driver.get("$url/read/20/test")
        verifyWhoToFollow()
    }

    private fun verifyWhoToFollow() {
        Thread.sleep(1000) // Wait for AJAX response
        assertElementCount(".who-to-follow .btn-follow", 3)
    }
}
