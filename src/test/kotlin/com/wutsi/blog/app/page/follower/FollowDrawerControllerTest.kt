package com.wutsi.blog.app.page.follower

import com.wutsi.blog.SeleniumMobileTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class FollowDrawerControllerTest : SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.POST, "/v1/view/search", HttpStatus.OK, "v1/view/search.json")
    }

    @Test
    fun `anonymous see see drawer`() {
        driver.get("$url/read/20/test")

        verifyDrawer()
    }

    @Test
    fun `non-subscriber can see drawer`() {
        login()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99.json")
        driver.get("$url/read/20/test")

        verifyDrawer()
    }

    @Test
    fun `subscriber cannot see drawer`() {
        login()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99.json")
        givenUserFollow(userId = 99, followerUserId = 1)
        driver.get("$url/read/20/test")

        verifyNoDrawer()
    }

    @Test
    fun `subscriber cannot see drawer in his story`() {
        login()

        driver.get("$url/read/20/test")

        verifyNoDrawer()
    }

    private fun verifyDrawer() {
        Thread.sleep(1000)

        assertElementVisible("#follow-drawer")
    }

    private fun verifyNoDrawer() {
        Thread.sleep(1000)

        assertElementCount("#follow-drawer", 0)
    }
}
