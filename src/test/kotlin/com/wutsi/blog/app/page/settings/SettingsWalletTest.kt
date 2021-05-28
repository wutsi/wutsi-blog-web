package com.wutsi.blog.app.page.settings

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.user.GetUserResponse
import com.wutsi.blog.client.user.MobileProvider.MTN
import com.wutsi.blog.client.user.WalletDto
import com.wutsi.blog.client.user.WalletType
import com.wutsi.blog.client.user.WalletType.INVALID
import com.wutsi.blog.client.user.WalletType.MOBILE
import com.wutsi.blog.fixtures.UserApiFixtures
import org.junit.Test

class SettingsWalletTest : SeleniumTestSupport() {
    @Test
    fun `setup wallet`() {
        // Goto settings page
        gotoPage()

        // Goto wallet page
        click("#wallet-card-none .btn-setup")
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.WALLET_EDIT)

        val wallet = createWalletDto()
        val user = UserApiFixtures.createUserDto(1, "foo", "Foo Bar", wallet = wallet)
        doReturn(GetUserResponse(user)).whenever(userApi).get(1L)

        select("#country", 1)
        input("#mobile-number", "664032997")
        select("#mobile-provider", 1)
        input("#full-name", "Ray Sponsible")
        click("#btn-submit")
        Thread.sleep(1000)

        // Back to settings
        assertCurrentPageIs(PageName.SETTINGS)
        assertElementCount("#wallet-card", 1)
    }

    @Test
    fun `deactivate wallet`() {
        val user = UserApiFixtures.createUserDto(1, "foo", "Foo Bar", wallet = createWalletDto())
        doReturn(GetUserResponse(user)).whenever(userApi).get(1L)

        gotoPage()

        // Deactivate
        val user1 = UserApiFixtures.createUserDto(1, "foo", "Foo Bar", wallet = createWalletDto(type = INVALID))
        doReturn(GetUserResponse(user1)).whenever(userApi).get(1L)

        click("#wallet-card .btn-delete")
        driver.switchTo().alert().accept()
        Thread.sleep(1000)

        // Back to settings
        assertCurrentPageIs(PageName.SETTINGS)
        assertElementPresent("#wallet-card-none")
    }

    @Test
    fun `update wallet`() {
        // Goto settings page - with wallet setup
        val user = UserApiFixtures.createUserDto(1, "foo", "Foo Bar", wallet = createWalletDto())
        doReturn(GetUserResponse(user)).whenever(userApi).get(1L)

        gotoPage()

        // Goto wallet page
        click("#wallet-card .btn-edit")
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.WALLET_EDIT)

        // Save wallet information
        select("#country", 1)
        input("#mobile-number", "664032998")
        select("#mobile-provider", 1)
        input("#full-name", "Ray Sponsible")
        click("#btn-submit")
        Thread.sleep(1000)

        // Back to settings
        assertCurrentPageIs(PageName.SETTINGS)
    }

    @Test
    fun `update wallet fails will display and error message`() {
        // Goto settings page - with wallet setup
        val user = UserApiFixtures.createUserDto(1, "foo", "Foo Bar", wallet = createWalletDto())
        doReturn(GetUserResponse(user)).whenever(userApi).get(1L)

        gotoPage()

        // Goto wallet page
        click("#wallet-card .btn-edit")
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.WALLET_EDIT)

        // Save wallet information
        doThrow(RuntimeException::class).whenever(userApi).wallet(any(), any())

        click("#btn-submit")
        Thread.sleep(1000)

        // Back to settings
        assertCurrentPageIs(PageName.WALLET_EDIT)
        assertElementVisible(".alert")
    }

    @Test
    fun `invalid phone number will display and error message`() {
        // Goto settings page - with wallet setup
        val user = UserApiFixtures.createUserDto(1, "foo", "Foo Bar", wallet = createWalletDto())
        doReturn(GetUserResponse(user)).whenever(userApi).get(1L)

        gotoPage()

        // Goto wallet page
        click("#wallet-card .btn-edit")
        Thread.sleep(1000)
        assertCurrentPageIs(PageName.WALLET_EDIT)

        // Save wallet information
        select("#country", 1)
        input("#mobile-number", "111")
        select("#mobile-provider", 1)
        input("#full-name", "Ray Sponsible")
        click("#btn-submit")
        Thread.sleep(1000)

        // Back to settings
        assertCurrentPageIs(PageName.WALLET_EDIT)
        assertElementVisible(".alert")
    }

    private fun gotoPage() {
        login()
        navigate("$url/me/settings")

        assertCurrentPageIs(PageName.SETTINGS)
    }

    private fun createWalletDto(type: WalletType = MOBILE) = WalletDto(
        fullName = "Ray Sponsible",
        country = "CM",
        mobileProvider = MTN,
        mobileNumber = "664032995",
        type = type
    )
}
