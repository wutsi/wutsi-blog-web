package com.wutsi.blog.app.page.login

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class RunAsControllerTest : SeleniumTestSupport() {
    override fun setUp() {
        super.setUp()

        givenUser(userId = 1, name = "ray.sponsible", superUser = true)
    }

    @Test
    fun `super user can run as another user`() {
        gotoPage()
        assertCurrentPageIs(PageName.RUN_AS)

        stub(HttpMethod.POST, "/v1/auth/as", HttpStatus.OK, "v1/session/as.json")
        stub(HttpMethod.GET, "/v1/auth/.*", HttpStatus.OK, "v1/session/get-session-run-as.json")
        input("#name", "john.smith")
        click("#btn-submit")

        assertCurrentPageIs(PageName.BLOG)
        assertElementPresent("nav.super-user")
        assertElementAttribute(".dropdown-item-user img", "title", "John Smith")
    }

    @Test
    fun `run as fails`() {
        gotoPage()
        assertCurrentPageIs(PageName.RUN_AS)

        stub(HttpMethod.POST, "/v1/auth/as", HttpStatus.CONFLICT)
        input("#name", "john.smith")
        click("#btn-submit")

        assertCurrentPageIs(PageName.RUN_AS)
        assertElementPresent(".alert")
    }

    @Test
    fun `logged in user cannot see run as menu`() {
        givenUser(userId = 1, name = "ray.sponsible", superUser = false)

        login()
        click("nav .nav-item")

        assertElementNotPresent("#navbar-runas")
    }

    @Test
    fun `logged in user cannot access run-as page`() {
        givenUser(userId = 1, name = "ray.sponsible", superUser = false)

        login()
        driver.get("$url/login/as")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `anonymous user cannot access run-as page`() {
        driver.get("$url/login/as")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    private fun gotoPage() {
        login()

        click("nav .nav-item")
        click("#navbar-runas")
    }
}
