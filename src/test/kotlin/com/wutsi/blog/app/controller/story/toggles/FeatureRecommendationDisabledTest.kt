package com.wutsi.blog.app.controller.story.toggles

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource


@TestPropertySource(
        properties = [
            "wutsi.toggles.recommendation=false"
        ]
)
class FeatureRecommendationDisabledTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")

        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }

    @Test
    fun `recommendation not visible`() {
        gotoPage(false)

        assertElementNotPresent("#recommendation-container")
    }


    fun gotoPage(login: Boolean) {
        login()
        click(".post a")
    }

}
