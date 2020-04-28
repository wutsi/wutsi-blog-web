package com.wutsi.blog.app.controller.login

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test


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
