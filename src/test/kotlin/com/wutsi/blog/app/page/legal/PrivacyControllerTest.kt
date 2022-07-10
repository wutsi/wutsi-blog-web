package com.wutsi.blog.app.page.legal

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.jupiter.api.Test

class PrivacyControllerTest : SeleniumTestSupport() {
    @Test
    fun `open privacy`() {
        driver.get("$url/privacy")
        assertCurrentPageIs(PageName.LEGAL_PRIVACY)
    }
}
