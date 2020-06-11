package com.wutsi.blog.app.controller.login

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class RunAsControllerTest: SeleniumTestSupport() {
    @Override
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/user/@/john.smith", HttpStatus.OK, "v1/user/get-user99.json")
        stub(HttpMethod.POST, "/v1/auth/as", HttpStatus.OK, "v1/session/as.json")
    }

    @Test
    fun `super user can run as another user`() {
        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-superuser.json")
        gotoPage()

        assertCurrentPageIs(PageName.RUN_AS)

        input("#name", "john.smith")
        click("#btn-submit")

        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `logged in user cannot see run as menu`() {
        login()
        click("nav .nav-item")

        assertElementNotPresent("#navbar-runas")
    }

    @Test
    fun `logged in user cannot access run-as page`() {
        login()
        driver.get("$url/login/as")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    @Test
    fun `anonymous user cannot access run-as page`() {
        login()
        driver.get("$url/login/as")

        assertCurrentPageIs(PageName.ERROR_403)
    }

    private fun gotoPage() {
        login()

        click("nav .nav-item")
        click("#navbar-runas")
    }
}
