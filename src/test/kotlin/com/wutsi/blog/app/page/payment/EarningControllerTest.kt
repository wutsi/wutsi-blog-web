package com.wutsi.blog.app.page.payment

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class EarningControllerTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/earning/search", HttpStatus.OK, "v1/earning/search.json")
        stub(HttpMethod.GET, "/v1/partner/user/1", HttpStatus.OK, "v1/partner/get.json")
    }

    @Test
    fun `user can view his earnings`() {
        gotoPage()

        Thread.sleep(5000)
        assertElementPresent(".tui-chart")
        assertElementPresent(".total-earning")
        assertElementNotPresent(".no-earning")
    }

    @Test
    fun `user has no earning`() {
        stub(HttpMethod.POST, "/v1/earning/search", HttpStatus.OK, "v1/earning/search_empty.json")
        gotoPage()

        assertElementNotPresent(".tui-chart")
        assertElementNotPresent(".total-earning")
        assertElementPresent(".no-earning")
    }

    @Test
    fun `invite user to join wpp`() {
        stub(HttpMethod.POST, "/v1/earning/search", HttpStatus.OK, "v1/earning/search_empty.json")
        stub(HttpMethod.GET, "/v1/partner/user/1", HttpStatus.NOT_FOUND)

        gotoPage()

        assertElementPresent("#wpp")
    }

    @Test
    fun `never invite contract to join wpp`() {
        givenContract(1)
        stub(HttpMethod.POST, "/v1/earning/search", HttpStatus.OK, "v1/earning/search_empty.json")
        stub(HttpMethod.GET, "/v1/partner/user/1", HttpStatus.NOT_FOUND)

        gotoPage()

        assertElementNotPresent("#wpp")
    }

    @Test
    fun `anonymous cannot view his earnings`() {
        driver.get("$url/me/earning")

        assertCurrentPageIs(PageName.LOGIN)
    }

    fun gotoPage() {
        login()
        click("nav .nav-item")
        click("#navbar-earnings")

        assertCurrentPageIs(PageName.EARNING)
    }
}
