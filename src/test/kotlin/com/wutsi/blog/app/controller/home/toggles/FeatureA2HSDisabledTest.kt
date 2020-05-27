package com.wutsi.blog.app.controller.home.toggles

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.add-to-homescreen=false"
        ]
)
class FeatureA2HSDisabledTest: SeleniumTestSupport() {
    @Test
    fun `add to homescreen headers`() {
        driver.get(url)

        assertElementNotPresent("link#a2hs-css")
        assertElementNotPresent("script#a2hs-code-1")
        assertElementNotPresent("script#a2hs-code-2")
    }
}
