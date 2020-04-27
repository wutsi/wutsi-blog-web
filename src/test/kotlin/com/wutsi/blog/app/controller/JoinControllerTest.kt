package com.wutsi.blog.app.controller

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test


class JoinControllerTest : SeleniumTestSupport() {
    @Test
    fun `should redirect on login page on join for anonymous users #1` () {
        navigate("$url/join")
        assertCurrentPageIs(PageName.LOGIN)
        assertElementNotPresent("#login-panel")
        assertElementPresent("#join-panel")
    }
}
