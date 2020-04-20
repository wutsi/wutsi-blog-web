package com.wutsi.blog.app.controller.user

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test


class JoinControllerTest : SeleniumTestSupport() {
    @Test
    fun `should redirect on login page on join for anonymous users #1` () {
        gotoPage()

        assertElementAttributeEndsWith("#btn-join-1", "href", "/me")

        click("#btn-join-1")

        assertCurrentPageIs(PageName.LOGIN)
        assertElementPresent("#join-panel")
    }


    @Test
    fun `should redirect on login page on join for anonymous users #2` () {
        gotoPage()

        assertElementAttributeEndsWith("#btn-join-2", "href", "/me")

        click("#btn-join-2")
        assertCurrentPageIs(PageName.LOGIN)
    }

    private fun gotoPage() {
        navigate("$url/join")
//        click("#navbar-join")

        assertCurrentPageIs(PageName.JOIN)

        assertElementNotPresent("#navbar-join")
    }
}
