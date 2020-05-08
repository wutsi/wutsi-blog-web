package com.wutsi.blog.app.controller.story

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class StoryDraftControllerTest: SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-draft.json")
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-draft.json")
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

        Thread.sleep(1000)
        assertElementText("#tab-draft .story-count", "11")
        assertElementCount(".story", 2)
    }

    @Test
    fun `user preview story`() {
        gotoPage()

        assertElementAttribute(".story:first-child .menu-item-preview", "target", "_new")

        click(".story:first-child .dropdown .btn")
        click(".story:first-child .menu-item-preview")

        Thread.sleep(1000)

        val parentWindow = driver.windowHandle
        driver.windowHandles.forEach{
            if (it != parentWindow) {
                driver.switchTo().window(it)
                assertCurrentPageIs(PageName.READ)
            }
        }
    }

    fun gotoPage(login: Boolean = true) {
        if (login){
            login()
            click("nav .nav-item")
            click("#navbar-draft")
            assertCurrentPageIs(PageName.STORY_DRAFT)
        } else {
            driver.get("$url/me/draft")
        }
    }
}
