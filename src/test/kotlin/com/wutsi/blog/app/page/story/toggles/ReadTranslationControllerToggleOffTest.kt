package com.wutsi.blog.app.page.story.toggles

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "wutsi.toggles.translation=false"
    ]
)
class ReadTranslationControllerToggleOffTest : SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
    }

    @Test
    fun `never show translation link when story language = user language`() {
        // Given
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")

        // When
        driver.get("$url/read/20/test")

        // Then
        assertCurrentPageIs(PageName.READ)

        assertElementCount("#translation-container", 0)
    }

    @Test
    fun `never show translation link when story language != user language`() {
        // Given
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published-fr.json")

        // When
        driver.get("$url/read/20/test")

        // Then
        assertCurrentPageIs(PageName.READ)

        assertElementCount("#translation-container", 0)
    }

    @Test
    fun `never translate a page`() {
        // When
        driver.get("$url/read/20/test?translate=fr")

        // Then
        assertCurrentPageIs(PageName.READ)

        assertElementAttribute("html", "lang", "en")
    }

    @Test
    fun `never translate a page for unsupported languages`() {
        // When
        driver.get("$url/read/20/test?translate=ro")

        // Then
        assertCurrentPageIs(PageName.READ)

        assertElementAttribute("html", "lang", "en")
    }
}
