package com.wutsi.blog.app.page.legal

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.jupiter.api.Test

class AboutControllerTest : SeleniumTestSupport() {
    @Test
    fun `open terms and condition`() {
        driver.get("$url/about")
        assertCurrentPageIs(PageName.LEGAL_ABOUT)
    }
}
