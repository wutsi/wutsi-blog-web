package com.wutsi.blog.app.page.story

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class StoryPublishedControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-draft.json")
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-published.json")
        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count.json")
    }


    @Test
    fun `anonymous user should not see published stories`() {
        gotoPage(false)

        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `user should see his published stories`() {
        gotoPage()

        Thread.sleep(1000)
        assertElementText("#tab-draft .story-count", "11")
        assertElementCount(".story", 3)
    }

    @Test
    fun `user with social links` () {
        gotoPage()

        assertElementNotPresent("#alert-no-social-link")
    }

    @Test
    fun `user with no social links` () {
        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user1-no-social-link.json")
        gotoPage()

        assertElementPresent("#alert-no-social-link")
        click("#btn-no-social-link")
        assertCurrentPageIs(PageName.SETTINGS)
    }


    fun gotoPage(login: Boolean = true) {
        if (login){
            login()
            click("nav .nav-item")
            click("#navbar-draft")
            click("#tab-published")
            assertCurrentPageIs(PageName.STORY_PUBLISHED)
        } else {
            driver.get("$url/me/published")
        }
    }
}
