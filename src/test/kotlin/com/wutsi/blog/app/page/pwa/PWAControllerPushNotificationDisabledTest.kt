package com.wutsi.blog.app.page.pwa

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.pwa-push-notification=false"
        ]
)
class PWAControllerPushNotificationDisabledTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }

    @Test
    fun `push notification in homepage`() {
        driver.get(url)
        assertCurrentPageIs(PageName.HOME)

        Thread.sleep(1000)
        assertElementNotPresent("script#firebase-app-js")
        assertElementNotPresent("script#firebase-messaging-js")
        assertElementNotPresent("script#firebase-js")
        assertElementNotPresent("#push-container")
    }

    @Test
    fun `push notification in reader`() {
        driver.get("$url/read/20/test")
        assertCurrentPageIs(PageName.READ)

        Thread.sleep(1000)
        assertElementNotPresent("script#firebase-app-js")
        assertElementNotPresent("script#firebase-messaging-js")
        assertElementNotPresent("script#firebase-js")
        assertElementNotPresent("#push-container")
    }

}
