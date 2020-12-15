package com.wutsi.blog.app.page.login

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test

class LoginControllerTest : SeleniumTestSupport() {
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
        givenUser(userId = 1, superUser = true)
        login()

        assertElementPresent("nav.super-user")
    }

    private fun validateButton(name: String) {
        assertElementAttributeEndsWith("#btn-$name", "href", "/login/$name")
        assertElementAttribute("#btn-$name", "wutsi-track-event", "login")
        assertElementAttribute("#btn-$name", "wutsi-track-value", name)
    }

    private fun gotoPage(error: Boolean = false, reason: String? = null, returnUrl: String? = null) {
        if (error) {
            driver.get("$url/login?error=invalid_client")
        } else {
            var loginUrl = "$url/login?"
            if (returnUrl != null) {
                loginUrl = "$loginUrl&return=$returnUrl"
            }
            if (reason != null) {
                loginUrl = "$loginUrl&reason=$reason"
            }
            driver.get(loginUrl)
        }
    }
}
