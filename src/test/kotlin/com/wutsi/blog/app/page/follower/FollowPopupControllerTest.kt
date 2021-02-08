package com.wutsi.blog.app.page.follower

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.fixtures.FollowerApiFixtures
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class FollowPopupControllerTest : SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
    }

    @Test
    fun `anonymous see see popup`() {
        driver.get("$url/read/20/test")

        verifyNoDrawer()
    }

    @Test
    fun `non-subscriber can see popup and follow`() {
        val response = FollowerApiFixtures.createSearchFollowerResponse(1L)
        doReturn(response).whenever(followerApi).create(any())

        login()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99.json")
        driver.get("$url/read/20/test")

        verifyDrawer()

        click("#follow-popup .btn-follow")

        Thread.sleep(15000) // It takes 10s to display the popup!
        assertElementNotVisible("#follow-popup")
        assertElementNotVisible(".navbar .btn-follow")
        assertElementNotVisible(".follow-panel")
    }

    @Test
    fun `non-subscriber can see popup and close`() {
        login()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99.json")
        driver.get("$url/read/20/test")

        verifyDrawer()

        click("#follow-popup .btn-close")

        Thread.sleep(15000) // It takes 10s to display the popup!
        assertElementNotVisible("#follow-popup")
        assertElementVisible(".navbar .btn-follow")
        assertElementVisible(".follow-panel")
    }

    @Test
    fun `subscriber cannot see popup`() {
        login()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-user99.json")
        givenUserFollow(userId = 99, followerUserId = 1)
        driver.get("$url/read/20/test")

        verifyNoDrawer()
    }

    @Test
    fun `subscriber cannot see popup in his story`() {
        login()

        driver.get("$url/read/20/test")

        verifyNoDrawer()
    }

    private fun verifyDrawer() {
        Thread.sleep(15000)

        assertElementVisible("#follow-popup")
        assertElementAttribute("#follow-popup .btn-follow", "wutsi-track-event", "follow_popup_click")
        assertElementAttribute("#follow-popup .btn-close", "wutsi-track-event", "follow_popup_close")
    }

    private fun verifyNoDrawer() {
        Thread.sleep(1000)

        assertElementCount("#follow-popup", 0)
    }
}
