package com.wutsi.blog.app.page.story

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class ReadTranslationControllerTest : SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
    }

    @Test
    fun `dont show translation link when story language = user language`() {
        // Given
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")

        // When
        driver.get("$url/read/20/test")

        // Then
        assertCurrentPageIs(PageName.READ)

        assertElementCount("#translation-container", 0)
    }

    @Test
    fun `dont show translation link when story language unsupported`() {
        // Given
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published-ro.json")

        // When
        driver.get("$url/read/20/test")

        // Then
        assertCurrentPageIs(PageName.READ)

        assertElementCount("#translation-container", 0)
    }

    @Test
    fun `show translation link when story language != user language`() {
        // Given
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published-fr.json")

        // When
        driver.get("$url/read/20/test")

        // Then
        assertCurrentPageIs(PageName.READ)

        assertElementAttributeEndsWith("#translation-container a.translation-url", "href", "/read/20/lorem-ipsum?translate=en")
        assertElementAttributeEndsWith("#translation-container a.translation-url", "wutsi-track-event", "translate")
        assertElementCount("#translation-container a.translation-original-url", 0)
    }

    @Test
    fun `show story translation`() {
        // Given
        stub(HttpMethod.GET, "/v1/story/20/translate.*", HttpStatus.OK, "v1/story/get-story20-published-fr.json")

        // When
        driver.get("$url/read/20/test?translate=fr")

        // Then
        assertCurrentPageIs(PageName.READ)

        assertElementCount("#translation-container a.translation-url", 0)
        assertElementAttributeEndsWith("#translation-container a.translation-original-url", "href", "/read/20/lorem-ipsum")
        assertElementAttributeEndsWith("#translation-container a.translation-original-url", "wutsi-track-event", "translate_back")

        assertElementAttribute("html", "lang", "en")
    }

    @Test
    fun `return 404 for translation of unsupported languages`() {
        // When
        driver.get("$url/read/20/test?translate=ro")

        // Then
        assertCurrentPageIs(PageName.ERROR_404)
    }
}
