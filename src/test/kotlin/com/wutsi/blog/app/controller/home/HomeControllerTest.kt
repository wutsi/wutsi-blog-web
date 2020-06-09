package com.wutsi.blog.app.controller.home

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class HomeControllerTest: SeleniumTestSupport() {
    @Test
    fun `empty home page`() {
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-empty.json")
        driver.get(url)

        assertCurrentPageIs(PageName.HOME)
        assertElementCount("#main-post", 0)
        assertElementCount("#featured-posts", 0)
        assertElementCount("#popular-posts", 0)
        assertElementCount("#featured-authors", 0)
    }

    @Test
    fun `user should view recent stories and authors in home page`() {
        driver.get(url)
        assertCurrentPageIs(PageName.HOME)
        assertElementCount("#main-post .post", 1)
        assertElementCount("#featured-posts .post", 4)
        assertElementCount("#popular-posts .post", 2)
        assertElementCount("#featured-authors .author", 5)
    }

    @Test
    fun `home page META headers`() {
        driver.get(url)
        assertElementAttribute("head title", "text", META_TITLE)
        assertElementPresent("head meta[name='description']")
        assertElementAttribute("head meta[name='robots']", "content", "all")
    }

    @Test
    fun `home page Google Analytics`() {
        driver.get(url)
        assertElementPresent("script#ga-code")
    }

    @Test
    fun `home page Facebook Pixel`() {
        driver.get(url)
        assertElementPresent("script#fb-pixel-code")
    }
}
