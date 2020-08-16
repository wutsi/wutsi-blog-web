package com.wutsi.blog.app.page.comment

import com.wutsi.blog.SeleniumMobileTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class CommentControllerTest: SeleniumMobileTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/comment/search", HttpStatus.OK, "v1/comment/search.json")
        stub(HttpMethod.POST, "/v1/comment", HttpStatus.OK, "v1/comment/create.json")

        stub(HttpMethod.GET, "/v1/story/20", HttpStatus.OK, "v1/story/get-story20-published.json")

        stub(HttpMethod.POST, "/v1/recommendation/search", HttpStatus.OK, "v1/recommendation/search.json")

        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")
    }

    @Test
    fun `home page showing comment count` () {
        driver.get(url)

        assertElementText("#comment-count-20", "3")
    }

    @Test
    fun `blog page showing comment count` () {
        driver.get("url/@/ray.sponsible")

        assertElementText("#comment-count-20", "3")
        assertElementText("#comment-count-21", "5")
        assertElementText("#comment-count-22", "1")
        assertElementText("#comment-count-23", "")
        assertElementText("#comment-count-24", "")
        assertElementText("#comment-count-25", "")
        assertElementText("#comment-count-26", "")
    }

    @Test
    fun `reader has comment icon with count`() {
        gotoPage()

        Thread.sleep(1000)
        assertElementPresent(".comment-tool .fa-comment-alt")
        assertElementText(".comment-tool .comment-count", "3 Commentaires")
    }

    @Test
    fun `reader has comment icon`() {
        stub(HttpMethod.POST, "/v1/comment/count", HttpStatus.OK, "v1/comment/count_0.json")
        gotoPage()

        Thread.sleep(5000)
        assertElementPresent(".comment-tool .fa-comment-alt")
        assertElementText(".comment-tool .comment-count", "Commente")
    }

    @Test
    fun `open reader with comment panel opened`() {
        driver.get("$url/read/20/test?comment=1")

        Thread.sleep(3000)
        assertElementCount("#comment-widget .comment", 3)
    }

    @Test
    fun `user can add a comment`() {
        gotoPage(true)

        click(".comment-tool a")

        Thread.sleep(3000)
        assertElementCount("#comment-widget .comment", 3)

        click("#comment-editor textarea")
        input("#comment-editor textarea", "new comment")

        stub(HttpMethod.POST, "/v1/comment/search", HttpStatus.OK, "v1/comment/search_4_comments.json")
        stub(HttpMethod.POST, "/v1/comment/count", HttpStatus.OK, "v1/comment/count_4.json")
        click("#comment-editor .btn")

        Thread.sleep(1000)
        assertElementCount("#comment-widget .comment", 4)

        click("#comment-widget .close")
        Thread.sleep(1000)
        assertElementText(".comment-tool .comment-count", "4 Commentaires")
    }

    @Test
    fun `anonymous adding comment`() {
        gotoPage()

        Thread.sleep(3000)
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
