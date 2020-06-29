package com.wutsi.blog.app.page.partner.toggles

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.wpp=false"
        ]
)
class FeatureWPPDisabledTest: SeleniumTestSupport() {
    @Test
    fun partnerMenuOff() {
        login()

        click("nav .nav-item")
        assertElementNotPresent("#navbar-wpp")
    }
}
