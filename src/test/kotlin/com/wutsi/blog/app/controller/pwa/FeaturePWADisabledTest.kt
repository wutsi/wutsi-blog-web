package com.wutsi.blog.app.controller.pwa

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.pwa=false"
        ]
)
class FeaturePWADisabledTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }

    @Test
    fun `pwa headers`() {
        driver.get(url)
        assertElementNotPresent("head link[rel='manifest']")
        assertElementNotPresent("script#pwa-code")
    }

    @Test
    fun `add to homescreen in homepage`() {
        driver.get(url)

        assertElementPresent("script#a2hs-code")
        assertElementNotPresent("link#a2hs-css")
        assertElementNotPresent("#a2hs-container")
    }

    @Test
    fun `add to homescreen in reader`() {
        driver.get("$url/read/20/test")

        assertElementPresent("script#a2hs-code")
        assertElementNotPresent("#a2hs-container")
    }

}
