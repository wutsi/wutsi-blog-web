package com.wutsi.blog.app.controller.user

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test


class MeControllerTest : SeleniumTestSupport() {
    @Test
    fun `should welcome author` () {
        gotoPage()
    }

    fun gotoPage() {
        login()
        driver.get("$url/me")
        assertCurrentPageIs(PageName.ME)
    }
}
