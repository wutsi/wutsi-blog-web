package com.wutsi.blog.app.component.survey

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.survey=false"
        ]
)
class SurveyControllerToggleOffTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }

    @Test
    fun `home page not showing survey` () {
        driver.get(url)

        Thread.sleep(1000)
        assertElementNotPresent("#survey-container")
    }

    @Test
    fun `read page not showing survey` () {
        driver.get("$url/read/20/test")

        Thread.sleep(1000)
        assertElementNotPresent("#survey-container")
    }

}
