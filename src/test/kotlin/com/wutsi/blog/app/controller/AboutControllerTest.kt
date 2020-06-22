package com.wutsi.blog.app.controller

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test

class AboutControllerTest: SeleniumTestSupport() {
    @Test
    fun `open terms and condition`() {
        driver.get("$url/about")
        assertCurrentPageIs(PageName.READ)
    }
}
