package com.wutsi.blog.app.controller.partner

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class PartnerControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/partner/user/1", HttpStatus.OK, "v1/partner/save.json")
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

        stub(HttpMethod.GET, "/v1/partner/user/1", HttpStatus.OK, "v1/partner/get.json")    // Partner saved
        select("#country", 1)
        input("#mobile-number", "664032997")
        select("#mobile-provider", 1)
        input("#full-name", "Ray Sponsible")
        input("#email", "ray.sponsible@gmail.com")
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
        stub(HttpMethod.POST, "/v1/partner/user/1", HttpStatus.INTERNAL_SERVER_ERROR)

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
        stub(HttpMethod.GET, "/v1/partner/user/1", HttpStatus.OK, "v1/partner/get.json")

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
        assertElementAttribute("head title", "text", "Wutsi Partner Program")
        assertElementPresent("head meta[name='description']")
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")
    }


    private fun gotoPage(){
        login()

        click("nav .nav-item")
        click("#navbar-wpp")
    }}
