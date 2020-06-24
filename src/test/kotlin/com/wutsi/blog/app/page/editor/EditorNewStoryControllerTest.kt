package com.wutsi.blog.app.page.editor

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class EditorNewStoryControllerTest: SeleniumTestSupport() {
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

        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
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
        assertCurrentPageIs(PageName.STORY_READABILITY)
        assertElementAttributeEndsWith("#btn-previous", "href", "/editor/20")
        click("#btn-next")

        assertCurrentPageIs(PageName.STORY_TAG)
        assertElementAttributeEndsWith("#btn-previous", "href", "/me/story/20/readability")
        assertElementNotPresent(".alert-danger")
        select("#topic-id", 1)

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-not-live.json")
        click("#btn-publish")

        assertCurrentPageIs(PageName.STORY_PUBLISHED)
        assertElementPresent("#alert-published")
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
    fun `user can edit and publish existing draft story`() {
        gotoPage()

        assertElementHasClass("#story-load-error", "hidden")
        assertElementHasNotClass("#story-editor", "hidden")

        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.STORY_READABILITY)
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

        click("nav .nav-item")

        if (new) {
            click("#navbar-editor")
        } else {
            click("#navbar-draft")
            click(".story:first-child .dropdown .btn")
            click(".story .menu-item-edit")
        }

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.EDITOR)
    }

    private fun gotoPage(storyId:Long) {
        login()

        driver.get("$url/editor/$storyId")

        Thread.sleep(1000)
        assertCurrentPageIs(PageName.EDITOR)
    }
}
