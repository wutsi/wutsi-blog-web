package com.wutsi.blog.app.controller

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test


class JoinControllerTest : SeleniumTestSupport() {
    @Test
    fun `should redirect on login page on join for anonymous users #1` () {
        gotoPage()

        click("#btn-join-1")

        assertCurrentPageIs(PageName.LOGIN)
        assertElementPresent("#create-blog-panel")
        assertElementPresent("#create-blog-wizard")
    }


    @Test
    fun `should redirect on login page on join for anonymous users #2` () {
        gotoPage()

        click("#btn-join-2")
        assertCurrentPageIs(PageName.LOGIN)
        assertElementPresent("#create-blog-panel")
        assertElementPresent("#create-blog-wizard")
    }

    private fun gotoPage() {
        navigate("$url/join")
//        click("#navbar-join")

        assertCurrentPageIs(PageName.JOIN)

        assertElementNotPresent("#navbar-join")
    }
}
