package com.wutsi.blog.app.page.home

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class HomeControllerTest: SeleniumTestSupport() {
    @Test
    fun `home page for anonymous user`() {
        login()
        driver.get(url)

        assertCurrentPageIs(PageName.HOME)
        assertElementCount("#value-prop", 0)
    }

    @Test
    fun `empty home page`() {
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-empty.json")
        login()
        driver.get(url)

        assertCurrentPageIs(PageName.HOME)
        assertElementCount("#main-post", 0)
        assertElementCount("#featured-posts", 0)
        assertElementCount("#popular-posts", 0)
        assertElementCount("#featured-authors", 0)
    }

    @Test
    fun `user should view recent stories and authors in home page`() {
        login()
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
        assertElementAttribute("html", "lang", "fr")
        assertElementPresent("head title")
        assertElementPresent("head meta[name='description']")
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")
        assertElementAttributeEndsWith("head meta[property='og:image']", "content", "/assets/wutsi/img/logo/logo_512x512.png")
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

    @Test
    fun `Schemas script`() {
        driver.get(url)
        assertElementPresent("script[type='application/ld+json']")
    }

}
