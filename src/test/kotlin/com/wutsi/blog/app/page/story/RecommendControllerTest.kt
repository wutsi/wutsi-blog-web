package com.wutsi.blog.app.page.story

import com.wutsi.blog.SeleniumMobileTestSupport
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class RecommendControllerTest : SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/story/recommend", HttpStatus.OK, "v1/story/recommend.json")

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")
    }

    @Test
    fun `show recommendations when available`() {
        driver.get("$url/read/20/looks-good")

        Thread.sleep(5000)
        assertElementCount("#recommendation-container .story-summary-card", 3)
        assertElementAttribute("#recommendation-container .story-summary-card a", "wutsi-track-event", "click")

        assertElementCount("#recommendation-container .btn-read-more", 1)
        assertElementAttribute("#recommendation-container .btn-read-more", "wutsi-track-event", "xread_more")
        assertElementAttributeEndsWith("#recommendation-container .btn-read-more", "href", "/@/ray.sponsible")
    }

    @Test
    fun `show no recommendations when none are available`() {
        stub(HttpMethod.POST, "/v1/story/recommend", HttpStatus.OK, "v1/story/recommend-none.json")

        driver.get("$url/read/20/looks-good")

        Thread.sleep(5000)
        assertElementCount("#recommendation-container .post", 0)
        assertElementCount("#recommendation-container .btn-read-more", 0)
    }
}
