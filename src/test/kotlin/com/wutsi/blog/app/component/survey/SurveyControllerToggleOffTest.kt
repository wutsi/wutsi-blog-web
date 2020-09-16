package com.wutsi.blog.app.component.survey

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.toggles.survey=false"
        ]
)
class SurveyControllerToggleOffTest : SeleniumTestSupport() {
    @Test
    fun `home page not showing survey` () {
        driver.get(url)

        Thread.sleep(1000)
        assertElementNotPresent("#survey-container")
    }

}
