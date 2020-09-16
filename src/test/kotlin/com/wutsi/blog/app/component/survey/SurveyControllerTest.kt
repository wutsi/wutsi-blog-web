package com.wutsi.blog.app.component.survey

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test

class SurveyControllerTest : SeleniumTestSupport() {
    @Test
    fun `home page showing survey` () {
        driver.get(url)

        Thread.sleep(1000)
        assertElementVisible("#survey-container")
        assertElementAttribute("#survey-container button", "data-url", "https://docs.google.com/forms/d/122232")

        click("#survey-container button")

        driver.get(url)
        assertElementNotVisible("#survey-container")
    }

}
