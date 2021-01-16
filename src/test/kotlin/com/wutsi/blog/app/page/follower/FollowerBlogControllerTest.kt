package com.wutsi.blog.app.page.follower

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test

class FollowerBlogControllerTest : SeleniumMobileTestSupport() {
    override fun setupSdk() {
        super.setupSdk()

        givenUser(99, name = "john.smith", fullName = "John Smith", blog = true)
    }

    @Test
    fun `user cannot follow his own blog`() {
        login()
        driver.get("$url/@/ray.sponsible")

        verifyNoFollowButtons()
        verifyWhoToFollow()
    }

    @Test
    fun `anonymous who try to follow a blog is redirected to login page`() {
        driver.get("$url/@/ray.sponsible")

        verifyFollowButtons()
        verifyWhoToFollow()

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `anonymous who go straight to follow URL is redirected to login page`() {
        driver.get("$url/@/ray.sponsible/follow")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `follower cannot re-follow a blog`() {
        givenUserFollow(99, 1)
        login()

        driver.get("$url/@/john.smith")

        verifyNoFollowButtons()
        verifyWhoToFollow()
    }

    @Test
    fun `non-follower user can follow a blog`() {
        login()

        driver.get("$url/@/john.smith")

        verifyFollowButtons("john.smith")

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.BLOG)
    }

    private fun verifyFollowButtons(userName: String = "ray.sponsible") {
        assertElementCount(".navbar .btn-follow", 1)
        assertElementAttribute(".navbar .btn-follow", "wutsi-track-event", "follow_click")

        assertElementCount(".follow-panel .btn-follow", 1)
        assertElementAttribute(".follow-panel .btn-follow", "wutsi-track-event", "follow_click")
    }

    private fun verifyNoFollowButtons() {
        assertElementCount(".navbar .btn-follow", 0)
        assertElementCount(".follow-panel .btn-follow", 0)
    }

    private fun verifyWhoToFollow() {
        Thread.sleep(1000) // Wait for AJAX response
        assertElementCount(".who-to-follow .btn-follow", 3)
    }
}
