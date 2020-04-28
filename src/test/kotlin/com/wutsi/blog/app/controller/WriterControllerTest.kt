package com.wutsi.blog.app.controller

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test


class WriterControllerTest : SeleniumTestSupport() {
    @Test
    fun `should redirect on login page on join for anonymous` () {
        gotoPage()

        click("#btn-join")

        assertCurrentPageIs(PageName.LOGIN)
        assertElementPresent("#create-blog-panel")
        assertElementPresent("#create-blog-wizard")
    }

    private fun gotoPage() {
        navigate("$url/writer")

        assertCurrentPageIs(PageName.WRITER)
    }
}
