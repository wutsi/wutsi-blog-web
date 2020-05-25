package com.wutsi.blog.app.controller.story

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class EditorPublishedStoryControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count.json")
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search.json")
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")
        stub(HttpMethod.POST, "/v1/story/20", HttpStatus.OK, "v1/story/save.json")
        stub(HttpMethod.POST, "/v1/story", HttpStatus.OK, "v1/story/save.json")
        stub(HttpMethod.GET, "/v1/story/[0-9]+/readability", HttpStatus.OK, "v1/story/readability.json")
        stub(HttpMethod.POST, "/v1/story/[0-9]+/publish", HttpStatus.OK, "v1/story/publish.json")
    }

    @Test
    fun `user can edit and republish story`() {
        gotoPage()

        assertElementHasClass("#story-load-error", "hidden")
        assertElementHasNotClass("#story-editor", "hidden")

        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.STORY_READABILITY)
        assertElementAttributeEndsWith("#btn-previous", "href", "/editor/20")
        click("#btn-next")

        assertCurrentPageIs(PageName.STORY_TAG)
        assertElementAttributeEndsWith("#btn-previous", "href", "/me/story/20/readability")
        assertElementNotPresent(".alert-danger")
        select("#topic-id", 1)

        click("#btn-publish")

        assertCurrentPageIs(PageName.STORY_PUBLISHED)
        assertElementPresent("#alert-published")
    }

    @Test
    fun `publish error`() {
        gotoPage()

        assertElementHasClass("#story-load-error", "hidden")
        assertElementHasNotClass("#story-editor", "hidden")

        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.STORY_READABILITY)
        assertElementAttributeEndsWith("#btn-previous", "href", "/editor/20")
        click("#btn-next")

        assertCurrentPageIs(PageName.STORY_TAG)
        assertElementNotPresent(".alert-danger")
        select("#topic-id", 1)

        stub(HttpMethod.POST, "/v1/story/20/publish", HttpStatus.INTERNAL_SERVER_ERROR)
        click("#btn-publish")

        assertCurrentPageIs(PageName.STORY_TAG)
        assertElementPresent(".alert-danger")
    }

    @Test
    fun `close editor`() {
        gotoPage()

        click("#btn-close")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.STORY_PUBLISHED)
    }


    private fun gotoPage() {
        login()

        click("nav .nav-item")

        click("#navbar-draft")
        click("#tab-published")
        click(".story:first-child .dropdown .btn")
        click(".story .menu-item-edit")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.EDITOR)
    }
}
