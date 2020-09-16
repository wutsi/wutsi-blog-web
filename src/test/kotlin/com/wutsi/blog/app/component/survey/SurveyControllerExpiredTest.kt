package com.wutsi.blog.app.component.survey

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "wutsi.survey.start-date=2010-01-01",
            "wutsi.survey.end-date=2010-02-01"
        ]
)
class SurveyControllerExpiredTest : SeleniumTestSupport() {
    @Test
    fun `home page not showing expired survey` () {
        driver.get(url)

        Thread.sleep(1000)
        assertElementNotVisible("#survey-container")
    }

}
