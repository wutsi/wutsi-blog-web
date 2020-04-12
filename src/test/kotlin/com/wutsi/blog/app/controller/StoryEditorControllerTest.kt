package com.wutsi.blog.app.controller

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Ignore
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class StoryEditorControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/10", HttpStatus.OK, "v1/story/get-story10-published.json")
        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-draft.json")
        stub(HttpMethod.GET, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")

        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
    }

    @Test
    fun `user can edit draft story`() {
        gotoPage(20)

        assertElementHasClass("#story-load-error", "hidden")
        assertElementHasNotClass("#story-editor", "hidden")
    }


    @Test
    @Ignore
    fun `user can publish draft story`() {
        gotoPage(20)

        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish")

        assertCurrentPageIs(PageName.STORY_PUBLISH)
    }

    @Test
    @Ignore
    fun `user can edit published story`() {
        gotoPage(10)

        assertElementHasNotClass("#story-load-error .permission-denied", "hidden")
        assertElementHasNotClass("#story-load-error .not-found", "hidden")
        assertElementHasNotClass("#story-load-error .unknwon", "hidden")
        assertElementHasNotClass("#story-load-error", "hidden")

        assertElementHasNotClass("#story-editor", "hidden")
    }

    @Test
    fun `anonymous user cannot edit stories`() {
        driver.get("$url/me/story/10/editor")

        assertCurrentPageIs(PageName.LOGIN)
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

    private fun gotoPage(storyId:Long) {
        login()

        driver.get("$url/me/story/$storyId/editor")
        Thread.sleep(1000)

        assertCurrentPageIs(PageName.STORY_EDITOR)
    }
}
