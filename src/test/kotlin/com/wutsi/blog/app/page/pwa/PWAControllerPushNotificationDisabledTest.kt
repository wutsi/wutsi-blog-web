package com.wutsi.blog.app.page.pwa

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.pwa-notification=false"
        ]
)
class PWAControllerPushNotificationDisabledTest: SeleniumTestSupport() {
    @Test
    fun `pwa headers`() {
        driver.get(url)

        assertElementNotPresent("script#firebase-app-js")
        assertElementNotPresent("script#firebase-messaging-js")
        assertElementNotPresent("script#firebase-js")
    }
}
