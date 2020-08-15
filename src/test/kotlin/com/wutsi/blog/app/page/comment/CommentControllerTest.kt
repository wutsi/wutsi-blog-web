package com.wutsi.blog.app.page.comment

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class CommentControllerTest: SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/comment/count\\?storyId=20", HttpStatus.OK, "v1/comment/count.json")
        stub(HttpMethod.POST, "/v1/comment/search", HttpStatus.OK, "v1/comment/search.json")
        stub(HttpMethod.POST, "/v1/comment", HttpStatus.OK, "v1/comment/create.json")

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")

        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")
    }

    @Test
    fun `comment icon with count`() {
        gotoPage()

        Thread.sleep(1000)
        assertElementPresent(".comment-tool .fa-comment-alt")
        assertElementText(".comment-tool .comment-text", "3 Commentaires")
    }

    @Test
    fun `comment icon`() {
        stub(HttpMethod.GET, "/v1/comment/count\\?storyId=20", HttpStatus.OK, "v1/comment/count_0_comment.json")
        gotoPage()

        Thread.sleep(5000)
        assertElementPresent(".comment-tool .fa-comment-alt")
        assertElementText(".comment-tool .comment-text", "Commente")
    }

    @Test
    fun `add a comment`() {
        gotoPage(true)

        click(".comment-tool a")

        Thread.sleep(3000)
        assertElementCount("#comment-widget .comment", 3)

        click("#comment-editor textarea")
        input("#comment-editor textarea", "new comment")

        stub(HttpMethod.POST, "/v1/comment/search", HttpStatus.OK, "v1/comment/search_4_comments.json")
        click("#comment-editor .btn")

        Thread.sleep(3000)
        assertElementCount("#comment-widget .comment", 4)

        click("#comment-widget .close")
        Thread.sleep(3000)
        assertElementText(".comment-tool .comment-text", "4 Commentaires")
    }

    @Test
    fun `comment panel opened`() {
        driver.get("$url/read/20/test?comment=1")

        Thread.sleep(3000)
        assertElementCount("#comment-widget .comment", 3)
    }

    @Test
    fun `anonymous user redirected to login`() {
        gotoPage()

        click(".comment-tool a")

        Thread.sleep(3000)
        assertElementCount("#comment-widget .comment", 3)

        click("#comment-editor textarea")

        assertCurrentPageIs(PageName.LOGIN)
    }

    fun gotoPage(login: Boolean = false) {
        if (login){
            login()
        }

        driver.get(url)
        click(".post a")
    }
}
