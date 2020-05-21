package com.wutsi.blog.app.controller.partner

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.wpp=false"
        ]
)
class PartnerControllerTest: SeleniumTestSupport() {
    @Test
    fun partnerMenuOff() {
        login()

        click("nav .nav-item")
        assertElementNotPresent("#navbar-wpp")
    }
}
