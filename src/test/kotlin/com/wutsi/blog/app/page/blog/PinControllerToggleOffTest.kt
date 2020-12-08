package com.wutsi.blog.app.page.blog

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.pin=false"
        ]
)
class PinControllerToggleOffTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")
        stub(HttpMethod.GET, "/v1/user/@/john.smith", HttpStatus.OK, "v1/user/get-user99.json")
    }

    override fun setupSdk() {
        givenPin()
        givenNoFollower()
        givenNoChannel()
    }

    @Test
    fun `no pinned story on my blog` () {
        gotoPage(true)

        assertNoPin()
    }

    @Test
    fun `anonymous cannot see pins` () {
        gotoPage()

        assertNoPin()
    }

    @Test
    fun `user cannot see pin of other user` () {
        gotoPage(true, "john.smith")

        assertNoPin()
    }

    private fun assertNoPin() {
        assertElementNotPresent(".story-card-pinned")
        assertElementNotPresent(".story-card .btn-pin")
    }

    private fun gotoPage(login: Boolean = false, username: String = "ray.sponsible") {
        if (login) {
            login()
        }
        driver.get("$url/@/${username}")


        assertCurrentPageIs(PageName.BLOG)
    }
}
