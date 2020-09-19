package com.wutsi.blog.app.component.like

import com.wutsi.blog.SeleniumMobileTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class LikeControllerTest: SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/like/search", HttpStatus.OK, "v1/like/search.json")
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")
    }

    @Test
    fun `home page showing like count` () {
        driver.get(url)

        Thread.sleep(5000)
        assertElementText("#like-count-20", "2")
        assertElementText("#like-count-21", "1")
        assertElementText("#like-count-22", "3")
        assertElementText(".like-badge #like-count-23", "")
        assertElementText(".like-badge #like-count-24", "")
        assertElementText(".like-badge #like-count-25", "")
        assertElementText(".like-badge #like-count-26", "")
    }

    @Test
    fun `blog page showing like count` () {
        driver.get("$url/@/ray.sponsible")

        Thread.sleep(5000)
        assertElementText("#like-count-20", "2")
        assertElementText("#like-count-21", "1")
        assertElementText("#like-count-22", "3")
        assertElementText(".like-badge #like-count-23", "")
        assertElementText(".like-badge #like-count-24", "")
        assertElementText(".like-badge #like-count-25", "")
        assertElementText(".like-badge #like-count-26", "")
    }
}