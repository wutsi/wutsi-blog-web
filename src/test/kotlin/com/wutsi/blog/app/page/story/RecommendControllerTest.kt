package com.wutsi.blog.app.page.story

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.similarity.dto.GetSimilarStoriesResponse
import com.wutsi.similarity.dto.SimilarStory
import com.wutsi.stats.dto.SearchViewResponse
import com.wutsi.stats.dto.View
import org.junit.Test
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
        val similars = listOf(
            SimilarStory(21, 1.0),
            SimilarStory(22, 0.9),
            SimilarStory(23, 0.9),
            SimilarStory(24, 0.9),
            SimilarStory(25, 0.9),
            SimilarStory(26, 0.9)
        )
        doReturn(GetSimilarStoriesResponse(similars)).whenever(similarityApi).getSimilarStories(any(), any())

        val views = listOf(
            View(storyId = 23),
            View(storyId = 24)
        )
        doReturn(SearchViewResponse(views)).whenever(statsApi)
            .views(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        driver.get("$url/read/20/looks-good")

        Thread.sleep(5000)
        assertElementCount("#recommendation-container .story-summary-card", 6)
        assertElementAttribute("#recommendation-container .story-summary-card a", "wutsi-track-event", "xread")

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

    @Test
    fun `show no recommendations on backend errors`() {
        val similars = listOf(
            SimilarStory(21, 1.0),
            SimilarStory(22, 0.9),
            SimilarStory(23, 0.9),
            SimilarStory(24, 0.9),
            SimilarStory(25, 0.9),
            SimilarStory(26, 0.9)
        )
        doReturn(GetSimilarStoriesResponse(similars)).whenever(similarityApi).getSimilarStories(any(), any())

        val views = listOf(
            View(storyId = 23),
            View(storyId = 24)
        )
        doThrow(RuntimeException::class).whenever(statsApi)
            .views(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        driver.get("$url/read/20/looks-good")

        Thread.sleep(5000)
        assertElementCount("#recommendation-container .post", 0)
    }
}
