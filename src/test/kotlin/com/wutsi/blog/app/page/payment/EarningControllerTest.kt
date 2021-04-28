package com.wutsi.blog.app.page.payment

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.earning.EarningApi
import com.wutsi.earning.dto.Earning
import com.wutsi.earning.dto.SearchEarningResponse
import org.junit.Test
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDate

class EarningControllerTest : SeleniumTestSupport() {
    @MockBean
    private lateinit var api: EarningApi

    override fun setupWiremock() {
        super.setupWiremock()

        doReturn(SearchEarningResponse()).whenever(api).userEarnings(any(), any())
        givenPartner()
    }

    @Test
    fun `user can view his earnings`() {
        val earning = Earning(
            userId = 1L,
            amount = 5000,
            date = LocalDate.now(),
            currency = "XAF"
        )
        doReturn(SearchEarningResponse(listOf(earning))).whenever(api).userEarnings(any(), any())

        gotoPage()

        Thread.sleep(5000)
        assertElementPresent(".tui-chart")
        assertElementPresent(".total-earning")
        assertElementNotPresent(".no-earning")
    }

    @Test
    fun `invite user to join wpp`() {
        givenNoPartner()

        gotoPage()

        assertElementPresent("#wpp")
    }

    @Test
    fun `never invite contractor to join wpp`() {
        givenContract(1)
        givenNoPartner()

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
