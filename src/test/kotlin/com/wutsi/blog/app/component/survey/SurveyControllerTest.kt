package com.wutsi.blog.app.component.survey

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class SurveyControllerTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }

    @Test
    fun `take survey from home page` () {
        login()
        driver.get(url)

        Thread.sleep(1000)
        assertElementVisible("#survey-container")
        assertElementAttribute("#survey-container button", "data-url", "https://docs.google.com/forms/d/122232")

        click("#survey-container button")

        Thread.sleep(1000)
        driver.get("$url/read/20/test")
        assertElementNotVisible("#survey-container")
    }

    @Test
    fun `close survey on home page` () {
        login()
        driver.get(url)

        Thread.sleep(1000)
        assertElementVisible("#survey-container")
        assertElementAttribute("#survey-container button", "data-url", "https://docs.google.com/forms/d/122232")

        click("#survey-container .close")

        Thread.sleep(1000)
        driver.get("$url/read/20/test")
        assertElementNotVisible("#survey-container")
    }

    @Test
    fun `take survey from read page` () {
        driver.get("$url/read/20/test")

        Thread.sleep(1000)
        assertElementVisible("#survey-container")
        assertElementAttribute("#survey-container button", "data-url", "https://docs.google.com/forms/d/122232")

        click("#survey-container button")

        Thread.sleep(1000)
        driver.get("$url/read/20/test")
        assertElementNotVisible("#survey-container")
    }


    @Test
    fun `close survey from read page` () {
        login()
        driver.get("$url/read/20/test")

        Thread.sleep(1000)
        assertElementVisible("#survey-container")
        assertElementAttribute("#survey-container button", "data-url", "https://docs.google.com/forms/d/122232")

        click("#survey-container .close")

        Thread.sleep(1000)
        driver.get(url)
        assertElementNotVisible("#survey-container")
    }
}
