package com.wutsi.blog.app.controller.user

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class MeControllerTest : SeleniumTestSupport() {
    @Test
    fun `my page` () {
        gotoPage()

        assertElementCount(".post", 4)
        assertElementNotPresent("#welcome")
    }

    @Test
    fun `welcome new user` () {
        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user1-first-login.json")

        gotoPage()

        assertElementPresent("#welcome")
        assertElementAttributeEndsWith("#btn-create-story", "href", "/editor")
        assertElementAttributeEndsWith("#btn-syndicate-story", "href", "/me/syndicate")

        assertElementCount(".post", 0)
    }

    @Test
    fun `anonymous user redirect to login` () {
        driver.get("$url/me")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `META headers`() {
        gotoPage()

        val title = "Ray Sponsible"
        val description = "Ray sponsible is a test user"

        assertElementAttribute("head title", "text", title)
        assertElementAttribute("head meta[name='description']", "content", description)
        assertElementAttribute("head meta[name='robots']", "content", "all")

        assertElementAttribute("head meta[property='og:title']", "content", title)
        assertElementAttribute("head meta[property='og:description']","content", description)
        assertElementAttribute("head meta[property='og:type']", "content", "profile")
        assertElementAttribute("head meta[property='og:url']", "content", "http://localhost:8081/u/ray.sponsible")
        assertElementAttribute("head meta[property='og:image']", "content", "https://avatars3.githubusercontent.com/u/39621277?v=4")
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")
    }

    fun gotoPage() {
        login()
        click("nav .nav-item")
        click("nav .dropdown-item-user a")

        assertCurrentPageIs(PageName.ME)
    }
}
