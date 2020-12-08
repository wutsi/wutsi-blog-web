package com.wutsi.blog.app.page.pwa

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "wutsi.toggles.pwa=false"
    ]
)
class PWAControllerDisabledToggleOff : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
//        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }

    @Test
    fun `pwa headers`() {
        driver.get(url)
        assertElementNotPresent("head link[rel='manifest']")
        assertElementNotPresent("script#pwa-code")
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

    @Test
    fun `push notification not visible in homepage`() {
        driver.get(url)
        assertCurrentPageIs(PageName.HOME)

        Thread.sleep(1000)
        assertElementNotPresent("script#firebase-app-js")
        assertElementNotPresent("script#firebase-messaging-js")
        assertElementNotPresent("script#firebase-js")
        assertElementNotPresent("#push-container")
    }

    @Test
    fun `push notification not visible in reader`() {
        driver.get("$url/read/20/test")
        assertCurrentPageIs(PageName.READ)

        Thread.sleep(1000)
        assertElementNotPresent("script#firebase-app-js")
        assertElementNotPresent("script#firebase-messaging-js")
        assertElementNotPresent("script#firebase-js")
        assertElementNotPresent("#push-container")
    }
}
