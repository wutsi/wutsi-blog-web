package com.wutsi.blog.app.component.like

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class LikeControllerTest : SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/like/search", HttpStatus.OK, "v1/like/search.json")
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")
    }

    @Test
    fun `blog page showing like count`() {
        driver.get("$url/@/ray.sponsible")

        Thread.sleep(1000)
        assertElementText("#like-count-20", "2")
        assertElementText("#like-count-21", "1")
        assertElementText("#like-count-22", "3")
        assertElementText(".like-badge #like-count-23", "")
        assertElementText(".like-badge #like-count-24", "")
        assertElementText(".like-badge #like-count-25", "")
        assertElementText(".like-badge #like-count-26", "")
    }

    @Test
    fun `user like a story`() {
        login()
        driver.get("$url/read/20/test")

        assertElementText(".like-widget .like-badge .like-count", "2")
        click(".like-widget .like-badge")
    }

    @Test
    fun `anonymous is redirected to login when liking`() {
        driver.get("$url/read/20/test")
        click(".like-widget .like-badge")

        assertCurrentPageIs(PageName.LOGIN)

        assertElementPresent(".return")
        assertElementAttributeEndsWith(".return", "href", "/read/20/lorem-ipsum")
    }
}
