package com.wutsi.blog.app.page.blog

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.jupiter.api.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "wutsi.toggles.pin=false"
    ]
)
class PinControllerToggleOffTest : SeleniumTestSupport() {
    override fun setupSdk() {
        super.setupSdk()

        givenPin()
        givenNoFollower()
        givenNoChannel()
    }

    @Test
    fun `no pinned story on my blog`() {
        gotoPage(true)

        assertNoPin()
    }

    @Test
    fun `anonymous cannot see pins`() {
        gotoPage()

        assertNoPin()
    }

    @Test
    fun `user cannot see pin of other user`() {
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
        driver.get("$url/@/$username")

        assertCurrentPageIs(PageName.BLOG)
    }
}
