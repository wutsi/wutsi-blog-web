package com.wutsi.blog.app.controller.login

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test


class LoginControllerTest: SeleniumTestSupport() {
    @Test
    fun `user login`() {
        gotoPage()

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

    private fun gotoPage() {
        driver.get(url)

        click("#navbar-login")
        assertElementNotPresent("#navbar-login")
    }
}
