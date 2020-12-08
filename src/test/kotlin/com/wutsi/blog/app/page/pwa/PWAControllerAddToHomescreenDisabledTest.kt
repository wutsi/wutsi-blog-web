package com.wutsi.blog.app.page.pwa

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "wutsi.toggles.pwa-add-to-homescreen=false"
    ]
)
class PWAControllerAddToHomescreenDisabledTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
    }

    @Test
    fun `add to homescreen not visible in homepage`() {
        driver.get(url)
        assertCurrentPageIs(PageName.HOME)

        assertElementPresent("script#a2hs-js")
        assertElementNotPresent("link#a2hs-css")
        assertElementNotPresent("#a2hs-container")
    }

    @Test
    fun `add to homescreen not visible in reader`() {
        driver.get("$url/read/20/test")
        assertCurrentPageIs(PageName.READ)

        assertElementPresent("script#a2hs-js")
        assertElementNotPresent("#a2hs-container")
    }
}
