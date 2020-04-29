package com.wutsi.blog.app.controller.story.toggles

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource


@TestPropertySource(
        properties = [
            "wutsi.toggles.publish=false"
        ]
)
class FeatureEditorDisabledTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count.json")
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-draft.json")
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-draft.json")
        stub(HttpMethod.POST, "/v1/story/20", HttpStatus.OK, "v1/story/save.json")
        stub(HttpMethod.POST, "/v1/story", HttpStatus.OK, "v1/story/save.json")
    }

    @Test
    fun `user cant publish from editor`() {
        gotoPage(true)

        assertElementNotPresent("#btn-publish")
    }

    @Test
    fun `user can edit and publish draft story`() {
        gotoPage(false)
        click(".story:first-child .dropdown .btn")

        assertElementCount(".menu-item-publish", 0)
    }


    private fun gotoPage(edit: Boolean) {
        login()

        click("nav .nav-item")
        if (edit) {
            click("#navbar-editor")
        } else {
            click("#navbar-draft")
        }
    }
}
