package com.wutsi.blog.app.page.story.toggles

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "wutsi.toggles.google-one-tap-sign-in=false"
    ]
)
class GoogleOneTapToggleOffTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
    }

    @Test
    fun `GoogleOneTap should not showup for anonymous user in Home`() {
        gotoHome(false)

        assertElementNotPresent("#g_id_onload")
        assertElementNotPresent("#g_one_tap_script")
        assertElementNotPresent("#g_one_tap_callback")
    }

    @Test
    fun `GoogleOneTap not should showup for logged-in user in Home`() {
        gotoHome(true)

        assertElementNotPresent("#g_id_onload")
        assertElementNotPresent("#g_one_tap_script")
        assertElementNotPresent("#g_one_tap_callback")
    }

    @Test
    fun `GoogleOneTap should not showup for anonymous user in Reader`() {
        gotoStory(false)

        assertElementNotPresent("#g_id_onload")
        assertElementNotPresent("#g_one_tap_script")
        assertElementNotPresent("#g_one_tap_callback")
    }

    @Test
    fun `GoogleOneTap not should showup for logged-in user in Reader`() {
        gotoStory(true)

        assertElementNotPresent("#g_id_onload")
        assertElementNotPresent("#g_one_tap_script")
        assertElementNotPresent("#g_one_tap_callback")
    }

    private fun gotoStory(login: Boolean) {
        if (login) {
            login()
            click(".story-card a")
        } else {
            driver.get("$url/read/20/foo")
        }
    }

    private fun gotoHome(login: Boolean) {
        if (login) {
            login()
        } else {
            driver.get(url)
        }
    }
}
