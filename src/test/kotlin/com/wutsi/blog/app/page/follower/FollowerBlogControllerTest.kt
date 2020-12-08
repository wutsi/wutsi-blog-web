package com.wutsi.blog.app.page.follower

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class FollowerBlogControllerTest: SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")
        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
        stub(HttpMethod.GET, "/v1/user/search", HttpStatus.OK, "v1/user/search.json")
    }

    @Test
    fun `user cannot follow his own blog` () {
        login()
        driver.get("$url/@/ray.sponsible")

        verifyNoFollowButtons()
        verifyWhoToFollow()
    }

    @Test
    fun `anonymous can follow any blog` () {
        driver.get("$url/@/ray.sponsible")

        verifyFollowButtons()
        verifyWhoToFollow()

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `follower cannot re-follow a blog` () {
        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user99.json")
        login()

        givenUserFollow(userId=1, followerUserId = 99)
        driver.get("$url/@/ray.sponsible")

        verifyNoFollowButtons()
        verifyWhoToFollow()
    }

    @Test
    fun `non-follower user can follow a blog` () {
        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user99.json")
        login()

        driver.get("$url/@/ray.sponsible")

        verifyFollowButtons()

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.BLOG)
    }


    private fun verifyFollowButtons(){
        assertElementCount(".navbar .btn-follow", 1)
        assertElementAttributeEndsWith(".navbar .btn-follow", "href", "/follow?userId=1&return=/@/ray.sponsible")
        assertElementAttribute(".navbar .btn-follow", "wutsi-track-event", "follow")

        assertElementCount(".follow-panel .btn-follow", 1)
        assertElementAttributeEndsWith(".follow-panel .btn-follow", "href", "/follow?userId=1&return=/@/ray.sponsible")
        assertElementAttribute(".follow-panel .btn-follow", "wutsi-track-event", "follow")
    }

    private fun verifyNoFollowButtons(){
        assertElementCount(".navbar .btn-follow", 0)
        assertElementCount(".follow-panel .btn-follow", 0)
    }

    private fun verifyWhoToFollow() {
        Thread.sleep(1000)      // Wait for AJAX response
        assertElementCount(".who-to-follow .btn-follow", 3)
    }
}
