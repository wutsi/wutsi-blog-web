package com.wutsi.blog.app.scenarios

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class StoryDraftControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        stub(HttpMethod.POST, "/v1/auth", HttpStatus.OK, "v1/session/login.json")
        stub(HttpMethod.GET, "/v1/auth/.*", HttpStatus.OK, "v1/session/get-session1.json")

        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search.json")
        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count.json")

        stub(HttpMethod.POST, "/v1/user/search", HttpStatus.OK, "v1/user/search.json")
    }


    @Test
    fun `anonymous user should not see draft stories`() {
        driver.get("$url/story/draft")

        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `user should see his draft stories`() {
        login()
        driver.get("$url/story/draft")

        assertCurrentPageIs(PageName.STORY_LIST_DRAFT)
        assertElementCount(".story", 4)
    }
}
