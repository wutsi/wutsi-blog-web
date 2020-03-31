package com.wutsi.blog.app.scenarios

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class StoryDraftTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search.json")
        stub(HttpMethod.POST, "/v1/user/search", HttpStatus.OK, "v1/user/search.json")

        stub(HttpMethod.GET, "/v1/user/[0-9]+", HttpStatus.OK, "v1/user/get.json")
    }


    @Test
    fun `anonymous user should not see draft stories`() {
        driver?.get("$url/story/draft")

        assertCurrentPageIs(PageName.LOGIN)
    }

//    @Test
//    fun `writer should see their draft stories`() {
//        login()
//        driver?.get("$url/story/draft")
//
//        assertCurrentPageIs(PageName.STORY_LIST_DRAFT)
//        assertElementCount(".story", 4)
//    }

    @Test
    fun `draft page should contains META headers`() {
        driver?.get("$url/story/draft")

        assertElementAttribute("head base", "href", "http://localhost:8081")
        assertElementAttribute("head title", "text", META_TITLE)
        assertElementAttribute("head meta[name='description']", "content", META_DESCRIPTION)
        assertElementAttribute("head meta[name='robots']", "content", "noindex,nofollow")
    }

}
