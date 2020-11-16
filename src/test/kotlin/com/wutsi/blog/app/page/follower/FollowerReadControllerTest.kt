package com.wutsi.blog.app.page.follower

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class FollowerReadControllerTest: SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/follower", HttpStatus.OK, "v1/follower/create.json")
        stub(HttpMethod.POST, "/v1/follower/search", HttpStatus.OK, "v1/follower/search-empty.json")

        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
    }

    @Test
    fun `anonymous can follow story and is redirected to login page` () {
        driver.get("$url/read/20/test")

        verifyFollowButtons()

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `logged-in user can follow story` () {
        login()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99.json")
        driver.get("$url/read/20/test")

        verifyFollowButtons()

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.READ)
    }

    @Test
    fun `user cannot follow it's own story` () {
        login()
        driver.get("$url/read/20/test")

        verifyNoFollowButtons()
    }

    @Test
    fun `follower cannot follow a story` () {
        login()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99.json")
        stub(HttpMethod.POST, "/v1/follower/search", HttpStatus.OK, "v1/follower/search-user99.json")
        driver.get("$url/read/20/test")

        verifyNoFollowButtons()
    }

    private fun verifyFollowButtons(){
        assertElementCount(".navbar .btn-follow", 1)
        assertElementAttributeEndsWith(".navbar .btn-follow", "href", "/read/20/lorem-ipsum")
        assertElementAttribute(".navbar .btn-follow", "wutsi-track-event", "follow")

        assertElementCount("#follow-container-1 .btn-follow", 1)
        assertElementAttributeEndsWith("#follow-container-1 .btn-follow", "href", "/read/20/lorem-ipsum")
        assertElementAttribute("#follow-container-1 .btn-follow", "wutsi-track-event", "follow")

        assertElementCount("#author-container .btn-follow", 1)
        assertElementAttributeEndsWith("#author-container .btn-follow", "href", "/read/20/lorem-ipsum")
        assertElementAttribute("#author-container .btn-follow", "wutsi-track-event", "follow")
    }

    private fun verifyNoFollowButtons(){
        assertElementCount(".navbar .btn-follow", 0)
        assertElementCount("#follow-container-1 .btn-follow", 0)
        assertElementCount("#author-container .btn-follow", 0)
    }
}
