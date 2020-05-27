package com.wutsi.blog.app.controller.home.toggles

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.add-to-homescreen=false"
        ]
)
class FeatureA2HSDisabledTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }

    @Test
    fun `add to homescreen in homepage`() {
        driver.get(url)

        assertElementNotPresent("link#a2hs-css")
        assertElementNotPresent("script#a2hs-code-1")
        assertElementNotPresent("script#a2hs-code-2")
        assertElementNotPresent("#a2hs-container")
    }

    @Test
    fun `add to homescreen in reader`() {
        driver.get("$url/read/20/test")

        Thread.sleep(1000)
        assertElementNotPresent("script#a2hs-code")
        assertElementNotPresent("#a2hs-container")
    }

}
