package com.wutsi.blog.app.controller.login

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class LoginControllerTest: SeleniumTestSupport() {
    @Test
    fun `user login`() {
        gotoPage()

        assertElementPresent("#login-panel")
        assertElementNotPresent("#create-blog-panel")
        assertElementNotPresent("#create-blog-wizard")
        assertElementNotPresent(".alert-danger")
        validateButton("google")
        validateButton("facebook")
        validateButton("github")
        validateButton("twitter")
    }

    @Test
    fun `login error`() {
        gotoPage(true)

        assertElementPresent("#login-panel")
        assertElementNotPresent("#create-blog-panel")
        assertElementNotPresent("#create-blog-wizard")
        assertElementPresent(".alert-danger")
        validateButton("google")
        validateButton("facebook")
        validateButton("github")
        validateButton("twitter")
    }

    @Test
    fun `super-user signs in`() {
        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-superuser.json")
        login()

        assertElementPresent("#super-user-banner")
    }

    private fun validateButton(name: String) {
        assertElementAttributeEndsWith("#btn-$name", "href", "/login/$name")
        assertElementAttribute("#btn-$name", "wutsi-track-event", "login")
        assertElementAttribute("#btn-$name", "wutsi-track-value", name)
    }

    private fun gotoPage(error: Boolean=false) {
        if (error){
            driver.get("$url/login?error=invalid_client")
        } else {
            driver.get("$url/login")
        }
    }
}
