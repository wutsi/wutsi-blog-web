package com.wutsi.blog.app.controller

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class StoryDraftControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()
        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count.json")
    }


    @Test
    fun `anonymous user should not see draft stories`() {
        gotoPage(false)

        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `user should see his draft stories`() {
        gotoPage()

        assertCurrentPageIs(PageName.STORY_LIST_DRAFT)
        assertElementCount(".story", 4)
    }

    fun gotoPage(login: Boolean = true) {
        if (login){
            login()
            click("nav .nav-item")
            click("#navbar-draft")
        } else {
            driver.get("$url/me/story/draft")
        }
    }
}
