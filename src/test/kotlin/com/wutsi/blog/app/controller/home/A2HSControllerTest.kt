package com.wutsi.blog.app.controller.home

import com.wutsi.blog.SeleniumMobileTestSupport
import org.junit.Test

class A2HSControllerTest: SeleniumMobileTestSupport() {
    @Test
    fun `add to homescreen headers`() {
        driver.get(url)

        assertElementPresent("script#a2hs-code")
    }
}
