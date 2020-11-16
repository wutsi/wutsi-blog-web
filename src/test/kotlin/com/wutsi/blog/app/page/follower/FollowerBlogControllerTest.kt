package com.wutsi.blog.app.page.follower

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class FollowerBlogControllerTest: SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/follower", HttpStatus.OK, "v1/follower/create.json")
        stub(HttpMethod.POST, "/v1/follower/search", HttpStatus.OK, "v1/follower/search-empty.json")

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

        stub(HttpMethod.POST, "/v1/follower/search", HttpStatus.OK, "v1/follower/search-user99.json")
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


    private fun verifyFollowButtons(blogId: Long = 1L){
        assertElementCount(".navbar .btn-follow", 1)
        assertElementAttributeEndsWith(".navbar .btn-follow", "href", "/follow?blogId=1&return=/@/ray.sponsible")
        assertElementAttribute(".navbar .btn-follow", "wutsi-track-event", "follow")

        assertElementCount(".navbar-author .btn-follow", 1)
        assertElementAttributeEndsWith(".navbar-author .btn-follow", "href", "/follow?blogId=1&return=/@/ray.sponsible")
        assertElementAttribute(".navbar-author .btn-follow", "wutsi-track-event", "follow")
    }

    private fun verifyNoFollowButtons(){
        assertElementCount(".navbar .btn-follow", 0)
        assertElementCount(".navbar-author .btn-follow", 0)
    }

    private fun verifyWhoToFollow() {
        Thread.sleep(1000)      // Wait for AJAX response
        assertElementCount(".who-to-follow .btn-follow", 3)
    }
}
