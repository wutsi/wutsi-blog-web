package com.wutsi.blog.app.page.login

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class LoginControllerTest: SeleniumTestSupport() {
    @Test
    fun `user login`() {
        gotoPage()

        assertElementNotPresent(".alert-danger")

        assertElementNotPresent(".return")

        assertElementPresent("#login-panel")

        validateButton("google")
        validateButton("facebook")
        validateButton("github")
        validateButton("twitter")
    }

    @Test
    fun `login error`() {
        gotoPage(true)

        assertElementPresent("#login-panel")
        assertElementPresent(".alert-danger")
        assertElementNotPresent(".return")
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

    private fun gotoPage(error: Boolean=false, reason: String?=null, returnUrl: String?=null) {
        if (error){
            driver.get("$url/login?error=invalid_client")
        } else {
            var loginUrl = "$url/login?"
            if (returnUrl != null){
                loginUrl = "$loginUrl&return=$returnUrl"
            }
            if (reason != null){
                loginUrl = "$loginUrl&reason=$reason"
            }
            driver.get(loginUrl)
        }
    }
}
