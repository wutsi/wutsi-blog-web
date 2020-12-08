package com.wutsi.blog.app.page.login

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test

class LogoutControllerTest : SeleniumTestSupport() {
    @Test
    fun `logout`() {
        login()

        driver.get("$url/logout")

        assertCurrentPageIs(PageName.HOME)
    }
}
