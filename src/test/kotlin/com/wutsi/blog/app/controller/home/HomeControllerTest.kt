package com.wutsi.blog.app.controller.home

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Before
import org.junit.Test


class HomeControllerTest: SeleniumTestSupport() {
    @Before
    override fun setUp(){
        super.setUp()

        driver.get("$url")
    }

    @Test
    fun `user should view recent stories in home page`() {
        assertCurrentPageIs(PageName.HOME)
        assertElementCount(".post", 4)
    }

    @Test
    fun `home page should contains META headers`() {
        assertElementAttribute("head title", "text", META_TITLE)
        assertElementAttribute("head meta[name='description']", "content", META_DESCRIPTION)
        assertElementAttribute("head meta[name='robots']", "content", "all")
    }
}
