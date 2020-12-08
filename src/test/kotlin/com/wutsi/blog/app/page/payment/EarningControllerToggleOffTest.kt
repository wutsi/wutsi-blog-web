package com.wutsi.blog.app.page.payment

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = ["wutsi.toggles.earning=false"]
)
class EarningControllerToggleOffTest : SeleniumTestSupport() {
    @Test
    fun `channel menu not available`() {
        login()

        click("nav .nav-item")
        assertElementNotPresent("#navbar-earnings")
    }

    @Test
    fun `channel page not accessible`() {
        login()
        driver.get("$url/me/earning")

        assertCurrentPageIs(PageName.ERROR_404)
    }
}
