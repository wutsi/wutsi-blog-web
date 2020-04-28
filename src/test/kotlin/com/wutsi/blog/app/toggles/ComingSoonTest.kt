package com.wutsi.blog.app.toggles

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource


@TestPropertySource(
        properties = [
            "wutsi.toggles.coming-soon=true"
        ]
)
class ComingSoonTest: SeleniumTestSupport() {
    @Test
    fun `user cant publish from editor`() {
        driver.get("$url")

        assertElementPresent("#coming-soon")
        assertElementAttribute("#link-twitter", "href", "https://www.twitter.com/wutsi2")
        assertElementAttribute("#link-facebook", "href", "https://www.facebook.com/2502022716689613")
    }
}
