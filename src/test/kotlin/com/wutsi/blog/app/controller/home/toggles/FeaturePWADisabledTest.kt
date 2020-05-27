package com.wutsi.blog.app.controller.home.toggles

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.pwa=false"
        ]
)
class FeaturePWADisabledTest: SeleniumTestSupport() {
    @Test
    fun `pwa headers`() {
        driver.get(url)
        assertElementNotPresent("head link[rel='manifest']")
        assertElementNotPresent("script#pwa-code")
    }
}
