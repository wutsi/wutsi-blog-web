package com.wutsi.blog.app.scenarios

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Ignore
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class StoryEditorControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        stub(HttpMethod.POST, "/v1/auth", HttpStatus.OK, "v1/session/login.json")
        stub(HttpMethod.GET, "/v1/auth/.*", HttpStatus.OK, "v1/session/get-session1.json")

        stub(HttpMethod.POST, "/v1/story/10", HttpStatus.OK, "v1/story/get-story10-published.json")
        stub(HttpMethod.POST, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-draft.json")
        stub(HttpMethod.POST, "/v1/story/99", HttpStatus.OK, "v1/story/get-story99-user99.json")
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search.json")

        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user1.json")
        stub(HttpMethod.GET, "/v1/user/99", HttpStatus.OK, "v1/user/get-user99.json")
        stub(HttpMethod.POST, "/v1/user/search", HttpStatus.OK, "v1/user/search.json")
    }

    @Test
    @Ignore
    fun `user can edit draft story`() {
        login()
        driver.get("$url/story/20/editor")

        assertCurrentPageIs(PageName.STORY_EDITOR)
        assertElementHasClass("#story-load-error", "hidden")
        assertElementHasNotClass("#story-editor", "hidden")
    }


    @Test
    @Ignore
    fun `user can publish draft story`() {
        login()
        driver.get("$url/story/20/editor")

        assertCurrentPageIs(PageName.STORY_EDITOR)
        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish")

        assertCurrentPageIs(PageName.STORY_PUBLISH)

    }

    @Test
    @Ignore
    fun `user can edit published story`() {
        login()
        driver.get("$url/story/10/editor")

        assertCurrentPageIs(PageName.STORY_EDITOR)
        assertElementHasNotClass("#story-load-error .permission-denied", "hidden")
        assertElementHasNotClass("#story-load-error .not-found", "hidden")
        assertElementHasNotClass("#story-load-error .unknwon", "hidden")
        assertElementHasNotClass("#story-load-error", "hidden")

        assertElementHasNotClass("#story-editor", "hidden")
    }

    @Test
    fun `anonymous user cannot edit stories`() {
        driver.get("$url/story/10/editor")

        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    @Ignore
    fun `user should never edit another user story`() {
        login()
        driver.get("$url/story/99/editor")

        assertCurrentPageIs(PageName.STORY_EDITOR)
        assertElementHasNotClass("#story-load-error .permission-denied", "hidden")
        assertElementHasClass("#story-load-error .not-found", "hidden")
        assertElementHasClass("#story-load-error .unknown", "hidden")
        assertElementHasClass("#story-load-error", "hidden")

        assertElementHasClass("#story-editor", "hidden")
    }

    @Test
    @Ignore
    fun `user should never edit invalid story`() {
        login()
        driver.get("$url/story/99999/editor")

        assertCurrentPageIs(PageName.STORY_EDITOR)
        assertElementHasNotClass("#story-load-error .permission-denied", "hidden")
        assertElementHasClass("#story-load-error .not-found", "hidden")
        assertElementHasNotClass("#story-load-error .unknown", "hidden")
        assertElementHasClass("#story-load-error", "hidden")

        assertElementHasClass("#story-editor", "hidden")
    }
}
