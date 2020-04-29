package com.wutsi.blog.app.controller.home.toggles

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource


@TestPropertySource(
        properties = [
            "wutsi.toggles.coming-soon=true"
        ]
)
class FeatureComingSoonTest: SeleniumTestSupport() {
    @Test
    fun `coming soon`() {
        driver.get("$url")

        assertElementPresent("#coming-soon")
        assertElementAttribute("#link-twitter", "href", "https://www.twitter.com/wutsi2")
        assertElementAttribute("#link-facebook", "href", "https://www.facebook.com/2502022716689613")
    }

    @Test
    fun `user subscribed`() {
        driver.get("$url/coming-soon/subscribed")
        driver.switchTo().alert().accept()

        assertElementPresent("#coming-soon")
        assertElementAttribute("#link-twitter", "href", "https://www.twitter.com/wutsi2")
        assertElementAttribute("#link-facebook", "href", "https://www.facebook.com/2502022716689613")
    }
}
