package com.wutsi.blog.app.controller.user

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class BlogControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")
    }

    @Test
    fun `blog page` () {
        gotoPage()

        assertElementAttribute(".author img", "src", "https://avatars3.githubusercontent.com/u/39621277?v=4")
        assertElementAttributeEndsWith(".author a", "href", "/@/ray.sponsible")
        assertElementAttribute(".author img", "src", "https://avatars3.githubusercontent.com/u/39621277?v=4")
        assertElementText(".author .bio", "Ray sponsible is a test user")
        assertElementAttribute(".author .facebook", "href", "https://www.facebook.com/ray.sponsible")
        assertElementAttribute(".author .twitter", "href", "https://www.twitter.com/raysponsible")
        assertElementAttribute(".author .linkedin", "href", "https://www.linkedin.com/in/ray.sponsible")

        assertElementCount(".post", 7)
        assertElementNotPresent("#create-first-story")
        assertElementNotPresent("#create-syndicate-story")
    }

    @Test
    fun `blog page with no post` () {
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-empty.json")

        gotoPage(true)

        Thread.sleep(1000)
        assertElementCount(".post", 0)
    }

    @Test
    fun `invite user to create post on his blog when empy` () {
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-empty.json")

        gotoPage(true)

        Thread.sleep(1000)
        assertElementPresent("#create-first-story")
        assertElementAttributeEndsWith("#btn-create-story", "href", "/editor")
        assertElementAttributeEndsWith("#btn-syndicate-story", "href", "/me/syndicate")
    }

    @Test
    fun `invite user to create post on other people blog when empy` () {
        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user99.json")
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-empty.json")

        gotoPage()

        Thread.sleep(1000)
        assertElementNotPresent("#create-first-story")
        assertElementNotPresent("#create-syndicate-story")
    }

    @Test
    fun `META headers`() {
        gotoPage()

        val title = "Ray Sponsible"
        val description = "Ray sponsible is a test user"

        assertElementAttribute("head title", "text", title)
        assertElementAttribute("head meta[name='description']", "content", description)
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")

        assertElementAttribute("head meta[property='og:title']", "content", title)
        assertElementAttribute("head meta[property='og:description']","content", description)
        assertElementAttribute("head meta[property='og:type']", "content", "profile")
        assertElementAttribute("head meta[property='og:url']", "content", "http://localhost:8081/@/ray.sponsible")
        assertElementAttribute("head meta[property='og:image']", "content", "https://avatars3.githubusercontent.com/u/39621277?v=4")
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")
    }

    fun gotoPage(login: Boolean = false) {
        if (login) {
            login()
            click("nav .nav-item")
            click("nav .dropdown-item-user a")
        } else {
            driver.get("$url/@/ray.sponsible")
        }


        assertCurrentPageIs(PageName.BLOG)
    }
}
