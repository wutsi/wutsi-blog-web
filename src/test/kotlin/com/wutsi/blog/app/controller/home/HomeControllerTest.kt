package com.wutsi.blog.app.controller.home

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class HomeControllerTest: SeleniumTestSupport() {
    @Before
    override fun setUp(){
        super.setUp()

        driver.get("$url")
    }

    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/view/search", HttpStatus.OK, "v1/view/search.json")
    }


    @Test
    fun `user should view recent stories in home page`() {
        assertCurrentPageIs(PageName.HOME)
        assertElementCount(".post", 4)
    }

    @Test
    fun `home page should contains META headers`() {
        assertElementAttribute("head title", "text", META_TITLE)
        assertElementPresent("head meta[name='description']")
        assertElementAttribute("head meta[name='robots']", "content", "all")
    }
}
