package com.wutsi.blog.app.controller.user

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class MeControllerTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")
    }

    @Test
    fun `anonymous user redirect to login` () {
        gotoPage(false)
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `logged user redirect to blog page` () {
        gotoPage(true)
        assertCurrentPageIs(PageName.BLOG)
    }

    fun gotoPage(login: Boolean) {
        if (login) {
            login()
            click("nav .nav-item")
            click("nav .dropdown-item-user a")
        } else {
            driver.get("$url/me")
        }
    }
}
