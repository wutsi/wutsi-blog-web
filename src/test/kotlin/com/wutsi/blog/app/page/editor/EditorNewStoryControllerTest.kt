package com.wutsi.blog.app.page.editor

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.channel.ChannelType
import com.wutsi.blog.client.channel.SearchChannelResponse
import com.wutsi.blog.fixtures.ChannelApiFixtures
import org.junit.Test
import org.mockito.Mockito
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class EditorNewStoryControllerTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count.json")
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-draft.json")
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-draft.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")
        stub(HttpMethod.POST, "/v1/story/20", HttpStatus.OK, "v1/story/save.json")
        stub(HttpMethod.POST, "/v1/story", HttpStatus.OK, "v1/story/save.json")
        stub(HttpMethod.GET, "/v1/story/[0-9]+/readability", HttpStatus.OK, "v1/story/readability.json")
        stub(HttpMethod.POST, "/v1/story/[0-9]+/publish", HttpStatus.OK, "v1/story/publish.json")
    }

    @Test
    fun `user can create and publish new story`() {
        gotoPage(true)

        assertElementHasClass("#story-load-error", "hidden")
        assertElementHasNotClass("#story-editor", "hidden")

        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.EDITOR_READABILITY)
        assertElementAttributeEndsWith("#btn-previous", "href", "/editor/20")
        click("#btn-next")

        assertCurrentPageIs(PageName.EDITOR_TAG)
        assertElementAttributeEndsWith("#btn-previous", "href", "/me/story/20/readability")
        assertElementNotPresent(".alert-danger")
        assertElementAttribute("#publish-now-radio", "checked", "true")
        assertElementAttribute("#scheduled-publish-date", "disabled", "true")
        assertElementAttribute("#publish-to-social-media-radio", "disabled", null)
        assertElementAttribute("#social-media-message", "disabled", "true")

        select("#topic-id", 1)
        input("#title", "This is title")
        input("#tagline", "This is tagline")
        input("#summary", "This is summary")

        click("#publish-to-social-media-radio")
        Thread.sleep(1000) // Delay before entering the message
        input("#social-media-message", "This is awesome!! #WutsiRocks")
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-draft.json")
        click("#btn-publish")

        assertCurrentPageIs(PageName.EDITOR_SHARE)
        assertElementAttribute("#btn-facebook", "wutsi-share-target", "facebook")
        assertElementAttribute("#btn-facebook", "wutsi-story-id", "20")

        assertElementAttribute("#btn-twitter", "wutsi-share-target", "twitter")
        assertElementAttribute("#btn-twitter", "wutsi-story-id", "20")

        assertElementAttribute("#btn-linkedin", "wutsi-share-target", "linkedin")
        assertElementAttribute("#btn-linkedin", "wutsi-story-id", "20")

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        click("#btn-read")
        assertCurrentPageIs(PageName.READ)
    }

    @Test
    fun `user can create and schedule when to publish new story`() {
        gotoPage(true)

        assertElementHasClass("#story-load-error", "hidden")
        assertElementHasNotClass("#story-editor", "hidden")

        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.EDITOR_READABILITY)
        click("#btn-next")

        assertCurrentPageIs(PageName.EDITOR_TAG)
        assertElementAttributeEndsWith("#btn-previous", "href", "/me/story/20/readability")
        assertElementNotPresent(".alert-danger")
        assertElementAttribute("#publish-now-radio", "checked", "true")
        assertElementAttribute("#scheduled-publish-date", "disabled", "true")
        assertElementAttribute("#publish-to-social-media-radio", "disabled", null)
        assertElementAttribute("#social-media-message", "disabled", "true")

        select("#topic-id", 1)
        input("#title", "This is title")
        input("#tagline", "This is tagline")
        input("#summary", "This is summary")

        click("#publish-later-radio")
        Thread.sleep(1000) // Delay before entering the message
        assertElementAttribute("#scheduled-publish-date", "disabled", null)

// Unable to set date in Jenkins :-(
//        driver.findElement(By.cssSelector("#scheduled-publish-date")).sendKeys(
//            "2030",
//            Keys.TAB,
//            "11",
//            "14"
//        )
//        click("#btn-publish")
//
//        assertCurrentPageIs(PageName.STORY_DRAFT)
    }

    @Test
    fun `share with channel Twitter`() {
        val response = SearchChannelResponse(
            channels = listOf(
                ChannelApiFixtures.createChannelDto(userId = 1, type = ChannelType.twitter)
            )
        )
        Mockito.`when`(channelApi.search(1L)).thenReturn(response)

        gotoPage(true)

        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.EDITOR_READABILITY)
        click("#btn-next")

        assertCurrentPageIs(PageName.EDITOR_TAG)
        select("#topic-id", 1)
        input("#tagline", "This is tagline")
        input("#summary", "This is summary")
        click("#btn-publish")

        assertCurrentPageIs(PageName.EDITOR_SHARE)
        assertElementNotPresent("#btn-twitter")
    }

    @Test
    fun `publish error`() {
        gotoPage(true)

        assertElementHasClass("#story-load-error", "hidden")
        assertElementHasNotClass("#story-editor", "hidden")

        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.EDITOR_READABILITY)
        assertElementAttributeEndsWith("#btn-previous", "href", "/editor/20")
        click("#btn-next")

        assertCurrentPageIs(PageName.EDITOR_TAG)
        assertElementNotPresent(".alert-danger")
        select("#topic-id", 1)
        input("#tagline", "This is tagline")
        input("#summary", "This is summary")

        stub(HttpMethod.POST, "/v1/story/20/publish", HttpStatus.INTERNAL_SERVER_ERROR)
        click("#btn-publish")

        assertCurrentPageIs(PageName.EDITOR_TAG)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `user can edit and publish existing draft story`() {
        gotoPage()

        assertElementHasClass("#story-load-error", "hidden")
        assertElementHasNotClass("#story-editor", "hidden")

        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.EDITOR_READABILITY)
    }

    @Test
    fun `close editor`() {
        gotoPage()

        click("#btn-close")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.STORY_DRAFT)
    }

    @Test
    fun `user should never edit another user story`() {
        gotoPage(99)

        assertElementHasNotClass("#story-load-error", "hidden")
        assertElementHasNotClass(".permission-denied", "hidden")
        assertElementHasClass(".not-found", "hidden")
        assertElementHasClass(".unknown", "hidden")

        assertElementHasClass("#story-editor", "hidden")
    }

    @Test
    fun `user should never edit invalid story`() {
        gotoPage(99999)

        assertElementHasNotClass("#story-load-error", "hidden")
        assertElementHasNotClass(".not-found", "hidden")
        assertElementHasClass(".permission-denied", "hidden")
        assertElementHasClass(".unknown", "hidden")

        assertElementHasClass("#story-editor", "hidden")
    }

    @Test
    fun `anonymous user cannot edit stories`() {
        driver.get("$url/me/story/10/editor")

        assertCurrentPageIs(PageName.LOGIN)
    }

    private fun gotoPage(new: Boolean = false) {
        login()

        driver.get("$url/editor")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.EDITOR)
    }

    private fun gotoPage(storyId: Long) {
        login()

        driver.get("$url/editor/$storyId")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.EDITOR)
    }
}
