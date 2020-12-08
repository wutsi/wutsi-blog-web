package com.wutsi.blog.app.page.settings

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.SearchChannelResponse
import com.wutsi.blog.fixtures.ChannelApiFixtures
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class SettingsControllerTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/user/1", HttpStatus.OK)
    }

    @Test
    fun `no subscriptions`() {
        gotoPage()

        assertElementPresent(".subscriptions-none")
        assertElementNotPresent("#subscriptions-container .author-summary-card")
    }

    @Test
    fun `my subscriptions`() {
        givenUserFollow(5, 1)

        gotoPage()

        assertElementPresent("#subscriptions-container .author-summary-card")
        assertElementPresent("#subscriptions-container .author-summary-card .btn-unfollow")
        assertElementNotPresent("#subscriptions-container .author-summary-card .btn-follow")
    }

    @Test
    fun `user change name`() {
        testUpdate("name", "ray.sponsible", "ray.sponsible-" + System.currentTimeMillis())
    }

    @Test
    fun `user cancel rename`() {
        testCancel("name", "ray.sponsible", "ray.sponsible-" + System.currentTimeMillis())
    }

    @Test
    fun `user change name - duplicate`() {
        stub(HttpMethod.POST, "/v1/user/1", HttpStatus.CONFLICT, "v1/user/error-duplicate_name.json")

        val error = "Désolé, ce nom est assigné à un autre utilisateur!"
        testUpdate("name", "ray.sponsible", "ray.sponsible-" + System.currentTimeMillis(), error)
    }

    @Test
    fun `user change blog name`() {
        testUpdate("full_name", "Ray Sponsible", "Ray Blog")
    }

    @Test
    fun `user cancel blog name`() {
        testCancel("full_name", "Ray Sponsible", "Ray Blog")
    }

    @Test
    fun `user can change email`() {
        testUpdate("email", "ray.sponsible@gmail.com", "ray.sponsible-" + System.currentTimeMillis() + "@gmail.com")
    }

    @Test
    fun `user cancel email`() {
        testCancel("email", "ray.sponsible@gmail.com", "ray.sponsible-" + System.currentTimeMillis() + "@gmail.com")
    }

    @Test
    fun `user change email - duplicate`() {
        stub(HttpMethod.POST, "/v1/user/1", HttpStatus.CONFLICT, "v1/user/error-duplicate_email.json")

        val error = "Désolé, cette addresse email est assignée à un autre utilisateur!"
        testUpdate("email", "ray.sponsible@gmail.com", "ray.sponsible-" + System.currentTimeMillis() + "@gmail.com", error)
    }

    @Test
    fun `user can change biography`() {
        testUpdate("biography", "Ray sponsible is a test user", "...")
    }

    @Test
    fun `user can cancel biography`() {
        testCancel("biography", "Ray sponsible is a test user", "...")
    }

    @Test
    fun `user update - unexpected error`() {
        stub(HttpMethod.POST, "/v1/user/1", HttpStatus.INTERNAL_SERVER_ERROR)

        val error = "Ooup! une erreur innatendue est survenue!"
        testUpdate("biography", "Ray sponsible is a test user", "...", error)
    }

    @Test
    fun `user can change website`() {
        testUpdate("website_url", "https://www.me.com/ray.sponsible", "https://www.avatar.com/ray.sponsible")
    }

    @Test
    fun `user can cancel website`() {
        testCancel("website_url", "https://www.me.com/ray.sponsible", "...")
    }

    @Test
    fun `user can connect to channels`() {
        gotoPage()

        assertElementPresent("#channel-twitter .btn-connect")
        assertElementPresent("#channel-facebook .btn-connect")
    }

    @Test
    fun `user can disconnect from channel`() {
        val response = SearchChannelResponse(
            channels = listOf(
                ChannelApiFixtures.createChannelDto(userId = 1, type = ChannelType.twitter),
                ChannelApiFixtures.createChannelDto(userId = 1, type = ChannelType.facebook)
            )
        )
        doReturn(response).whenever(channelApi).search(1L)

        gotoPage()

        assertElementPresent("#channel-twitter .btn-disconnect")
        assertElementPresent("#channel-facebook .btn-disconnect")

        click("#channel-twitter .btn-disconnect")
        assertCurrentPageIs(PageName.SETTINGS)
    }

    private fun testUpdate(name: String, originalValue: String, newValue: String, error: String? = null) {
        val selector = "#$name-form"

        gotoPage()
        assertElementAttribute("$selector .form-control", "value", originalValue)

        click("$selector .btn-edit")
        input("$selector .form-control", newValue)
        click("$selector .btn-save")

        Thread.sleep(1000)
        assertElementAttribute("$selector .form-control", "value", newValue)
        if (error == null) {
            assertElementHasClass("$selector .alert-danger", "hidden")
        } else {
            assertElementHasNotClass("$selector .alert-danger", "hidden")
            assertElementPresent("$selector .alert-danger")
        }
    }

    private fun testCancel(name: String, originalValue: String, newValue: String) {
        val selector = "#$name-form"

        gotoPage()
        assertElementAttribute("$selector .form-control", "value", originalValue)

        click("$selector .btn-edit")
        input("$selector .form-control", newValue)
        click("$selector .btn-cancel")
        assertElementAttribute("$selector .form-control", "value", originalValue)
        assertElementHasClass("$selector .alert-danger", "hidden")
    }

    private fun gotoPage() {
        login()

        click("nav .nav-item")
        click("#navbar-settings")

        assertCurrentPageIs(PageName.SETTINGS)
    }
}
