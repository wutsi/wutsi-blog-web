package com.wutsi.blog.app.page.partner

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.fixtures.PartnerApiFixtures
import com.wutsi.core.exception.InternalErrorException
import org.junit.Test

class PartnerControllerTest : SeleniumTestSupport() {
    @Test
    fun anonymousCanAccessWPP() {
        gotoPage(false)
        assertCurrentPageIs(PageName.PARTNER)
    }

    override fun setupSdk() {
        super.setupSdk()

        val response = PartnerApiFixtures.createSavePartnerResponse()
        doReturn(response).whenever(partnerApi).save(any(), any())
    }

    @Test
    fun registerToWPP() {
        gotoPage()
        assertCurrentPageIs(PageName.PARTNER)

        click("#btn-join")
        assertCurrentPageIs(PageName.PARTNER_JOIN)

        click("#btn-payment")
        assertCurrentPageIs(PageName.PARTNER_PAYMENT)
        assertElementNotPresent(".alert-danger")

        select("#country", 1)
        input("#mobile-number", "664032997")
        select("#mobile-provider", 1)
        input("#full-name", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")

        givenPartner() // Partner Saved
        click("#btn-submit")
        assertCurrentPageIs(PageName.PARTNER_SUCCESS)
    }

    @Test
    fun registerToWPPInvalidPhoneNumber() {
        gotoPage()
        assertCurrentPageIs(PageName.PARTNER)

        click("#btn-join")
        assertCurrentPageIs(PageName.PARTNER_JOIN)

        click("#btn-payment")
        assertCurrentPageIs(PageName.PARTNER_PAYMENT)

        select("#country", 1)
        input("#mobile-number", "000000000")
        select("#mobile-provider", 1)
        input("#full-name", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        click("#btn-submit")

        assertElementPresent(".alert-danger")
        assertCurrentPageIs(PageName.PARTNER_PAYMENT)
    }

    @Test
    fun registerToWPPBackendError() {
        doThrow(InternalErrorException("erorr")).whenever(partnerApi).save(any(), any())

        gotoPage()
        assertCurrentPageIs(PageName.PARTNER)

        click("#btn-join")
        assertCurrentPageIs(PageName.PARTNER_JOIN)

        click("#btn-payment")
        assertCurrentPageIs(PageName.PARTNER_PAYMENT)

        select("#country", 1)
        input("#mobile-number", "664032997")
        select("#mobile-provider", 1)
        input("#full-name", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        click("#btn-submit")

        assertElementPresent(".alert-danger")
        assertCurrentPageIs(PageName.PARTNER_PAYMENT)
    }

    @Test
    fun updateWPPAccount() {
        givenPartner()

        gotoPage()
        assertCurrentPageIs(PageName.PARTNER)

        click("#btn-join")
        assertCurrentPageIs(PageName.PARTNER_JOIN)

        click("#btn-payment")
        assertCurrentPageIs(PageName.PARTNER_PAYMENT)
        assertElementNotPresent("alert-danger")

        select("#country", 1)
        input("#mobile-number", "664032997")
        select("#mobile-provider", 1)
        input("#full-name", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")
        click("#btn-submit")
        assertCurrentPageIs(PageName.PARTNER_SUCCESS)
    }

    @Test
    fun `home page META headers`() {
        gotoPage()
        assertElementAttribute("html", "lang", "fr")
        assertElementAttribute("head title", "text", "Wutsi Partner Program | Wutsi")
        assertElementPresent("head meta[name='description']")
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")
    }

    private fun gotoPage(login: Boolean = true) {
        if (login) {
            login()
            click("nav .nav-item")
            click("#navbar-wpp")
        } else {
            driver.get("$url/partner")
        }
    }
}
