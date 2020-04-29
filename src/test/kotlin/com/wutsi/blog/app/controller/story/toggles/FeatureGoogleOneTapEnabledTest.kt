package com.wutsi.blog.app.controller.story.toggles

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource


@TestPropertySource(
        properties = [
            "wutsi.toggles.google-one-tap-sign-in=true"
        ]
)
class FeatureGoogleOneTapEnabledTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
    }


    @Test
    fun `GoogleOneTab should showup for anonymous user`() {
        gotoPage(false)

        assertElementPresent("#g_id_onload")
        assertElementPresent("#g_one_tap_script")
        assertElementPresent("#g_one_tap_callback")
    }

    @Test
    fun `GoogleOneTab not should showup for anonymous user`() {
        gotoPage(true)

        assertElementNotPresent("#g_id_onload")
        assertElementNotPresent("#g_one_tap_script")
        assertElementNotPresent("#g_one_tap_callback")
    }

    fun gotoPage(login: Boolean) {
        if (login) {
            login()
        } else {
            driver.get(url)
        }
        click(".post a")
    }

}
