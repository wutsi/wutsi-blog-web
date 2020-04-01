package com.wutsi.blog.app.scenarios

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class HomeControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search.json")
        stub(HttpMethod.POST, "/v1/user/search", HttpStatus.OK, "v1/user/search.json")
    }

    @Before
    override fun setUp(){
        super.setUp()

        driver.get("$url")
    }

    @Test
    fun `anonymous user should view login button in home page`() {
        assertElementPresent("nav .login")
        assertElementAttribute("nav .login", "rel", "nofollow")
    }

    @Test
    fun `user should view recent stories in home page`() {
        assertCurrentPageIs(PageName.HOME)
        assertElementCount(".post", 4)
    }

    @Test
    fun `home page should contains META headers`() {
        assertElementAttribute("head base", "href", "http://localhost:8081/")
        assertElementAttribute("head title", "text", META_TITLE)
        assertElementAttribute("head meta[name='description']", "content", META_DESCRIPTION)
        assertElementAttribute("head meta[name='robots']", "content", "all")
    }
}
