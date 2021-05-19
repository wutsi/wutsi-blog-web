package com.wutsi.blog.app.page.subscription

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.follower.CreateFollowerResponse
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class SubscribeControllerFreeTest : SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")
    }

    override fun setupSdk() {
        super.setupSdk()

        givenUser(99, name = "john.smith", fullName = "John Smith", blog = true)
    }

    @Test
    fun `Free - anonymous can follow a blog`() {
        driver.get("$url/@/ray.sponsible")

        verifyFollowButtons()

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.SUBSCRIPTION)

        assertElementCount(".btn-follow", 0)
        assertElementCount("#btn-google", 1)
    }

    @Test
    fun `Free - anonymous can follow a blog from Story reader`() {
        driver.get("$url/read/20/test")

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.SUBSCRIPTION)

        assertElementCount(".btn-follow", 0)
        assertElementCount("#btn-google", 1)
    }

    @Test
    fun `Free - anonymous can skip a blog`() {
        driver.get("$url/read/20/test")

        verifyFollowButtons()

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.SUBSCRIPTION)

        assertElementAttributeEndsWith("#btn-no-thanks", "href", "/@/ray.sponsible")
        click("#btn-no-thanks")
        assertCurrentPageIs(PageName.READ)
    }

    @Test
    fun `Free - anonymous can skip a blog from Story reader`() {
        driver.get("$url/@/ray.sponsible")

        verifyFollowButtons()

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.SUBSCRIPTION)

        assertElementAttributeEndsWith("#btn-no-thanks", "href", "/@/ray.sponsible")
        click("#btn-no-thanks")
        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `Free - user cannot follow his own blog`() {
        login()
        driver.get("$url/@/ray.sponsible")

        verifyNoFollowButtons()
    }

    @Test
    fun `Free - user can follow a blog`() {
        login()

        driver.get("$url/@/john.smith")

        verifyFollowButtons("john.smith")

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.SUBSCRIPTION)

        assertElementCount(".btn-follow", 1)
        assertElementCount("#btn-google", 0)
        assertElementAttributeEndsWith("#btn-no-thanks", "href", "/@/john.smith")

        doReturn(CreateFollowerResponse(111)).whenever(followerApi).create(any())
        click(".btn-follow")
        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `Free - user can follow a blog from a Story reader`() {
        login()
        driver.get("$url/@/john.smith")

        verifyFollowButtons("john.smith")

        click(".navbar .btn-follow")
        assertCurrentPageIs(PageName.SUBSCRIPTION)

        assertElementCount(".btn-follow", 1)
        assertElementCount("#btn-google", 0)
        assertElementAttributeEndsWith("#btn-no-thanks", "href", "/@/john.smith")

        doReturn(CreateFollowerResponse(111)).whenever(followerApi).create(any())
        click(".btn-follow")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `Free - user cannot re-follow a blog`() {
        givenUserFollow(99, 1)
        login()

        driver.get("$url/@/john.smith")

        verifyNoFollowButtons()
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
}
