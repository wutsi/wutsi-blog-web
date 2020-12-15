package com.wutsi.blog.app.page.blog

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test

class PinControllerTest : SeleniumTestSupport() {
    @Test
    fun `pinned story on my blog`() {
        givenPin()

        gotoPage(true)

        assertElementCount("#my-stories .story-card", 7)

        assertElementHasClass("#my-stories .story-card:first-child", "story-card-pinned")
        assertElementAttribute("#my-stories .story-card:first-child", "id", "story-card-21")

        assertElementHasNotClass("#story-card-20", "story-card-pinned")
        assertElementAttributeEndsWith("#story-card-20 .btn-pin", "href", "/pin/add?storyId=20")
        assertElementAttribute("#story-card-20 .btn-pin", "wutsi-track-event", "pin")

        assertElementHasClass("#story-card-21", "story-card-pinned")
        assertElementAttributeEndsWith("#story-card-21 .btn-pin", "href", "/pin/remove")
        assertElementAttribute("#story-card-21 .btn-pin", "wutsi-track-event", "unpin")
    }

    @Test
    fun `no pinned story on my blog`() {
        gotoPage(true)

        assertElementCount("#my-stories .story-card", 7)
        assertElementHasNotClass("#my-stories .story-card:first-child", "story-card-pinned")

        assertElementHasNotClass("#story-card-20", "story-card-pinned")
        assertElementAttributeEndsWith("#story-card-20 .btn-pin", "href", "/pin/add?storyId=20")
        assertElementAttribute("#story-card-20 .btn-pin", "wutsi-track-event", "pin")

        assertElementHasNotClass("#story-card-21", "story-card-pinned")
        assertElementAttributeEndsWith("#story-card-21 .btn-pin", "href", "/pin/add?storyId=21")
        assertElementAttribute("#story-card-21 .btn-pin", "wutsi-track-event", "pin")
    }

    @Test
    fun `anonymous cannot see pins`() {
        givenPin()

        gotoPage()

        assertNoPin()
    }

    @Test
    fun `user cannot see pin of other user`() {
        givenPin()

        gotoPage(true, "john.smith")

        assertNoPin()
    }

    protected fun assertNoPin() {
        assertElementNotPresent(".story-card-pinned")
        assertElementNotPresent(".story-card .btn-pin")
    }

    private fun gotoPage(login: Boolean = false, username: String = "ray.sponsible") {
        if (login) {
            login()
        }
        driver.get("$url/@/$username")

        assertCurrentPageIs(PageName.BLOG)
    }
}
