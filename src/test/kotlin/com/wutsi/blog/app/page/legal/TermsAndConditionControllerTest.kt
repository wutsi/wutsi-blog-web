package com.wutsi.blog.app.page.legal

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test

class TermsAndConditionControllerTest : SeleniumTestSupport() {
    @Test
    fun `open terms and condition`() {
        driver.get("$url/terms")
        assertCurrentPageIs(PageName.LEGAL_TERMS)
    }
}
