package com.wutsi.blog.app.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.SearchChannelResponse
import com.wutsi.blog.fixtures.ChannelApiFixtures
import com.wutsi.blog.fixtures.UserApiFixtures
import com.wutsi.core.exception.ConflictException
import org.junit.jupiter.api.Test

class SettingsControllerTest : SeleniumTestSupport() {
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
        doThrow(ConflictException("duplicate_name")).whenever(userApi).set(any(), any())

        val error = "Désolé, ce nom est assigné à un autre utilisateur!"
        testUpdate("name", "ray.sponsible", "ray.sponsible-" + System.currentTimeMillis(), error)
    }

    @Test
    fun `user change blog name`() {
        testUpdate("full_name", "Ray Sponsible", "Ray Blog")
    }

    @Test
    fun `user can change email`() {
        testUpdate("email", "ray.sponsible@gmail.com", "ray.sponsible-" + System.currentTimeMillis() + "@gmail.com")
    }

    @Test
    fun `user change email - duplicate`() {
        doThrow(ConflictException("duplicate_email")).whenever(userApi).set(any(), any())

        val error = "Désolé, cette addresse email est assignée à un autre utilisateur!"
        testUpdate(
            "email",
            "ray.sponsible@gmail.com",
            "ray.sponsible-" + System.currentTimeMillis() + "@gmail.com",
            error
        )
    }

    @Test
    fun `user can change biography`() {
        testUpdate("biography", UserApiFixtures.DEFAULT_BIOGRAPHY, "...")
    }

    @Test
    fun `user can change website`() {
        testUpdate("website_url", "https://www.me.com/ray.sponsible", "https://www.avatar.com/ray.sponsible")
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

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.SETTINGS)
    }
}
