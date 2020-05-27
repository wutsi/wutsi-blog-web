package com.wutsi.blog.app.controller.home

import com.wutsi.blog.SeleniumMobileTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class A2HSControllerTest: SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }

    @Test
    fun `add to homescreen in homepage`() {
        driver.get(url)

        Thread.sleep(1000)
        assertElementPresent("script#a2hs-code")
        assertElementPresent("#a2hs-container")
    }

    @Test
    fun `add to homescreen in reader`() {
        driver.get("$url/read/20/test")

        Thread.sleep(1000)
        assertElementPresent("script#a2hs-code")
        assertElementPresent("#a2hs-container")
    }
}
