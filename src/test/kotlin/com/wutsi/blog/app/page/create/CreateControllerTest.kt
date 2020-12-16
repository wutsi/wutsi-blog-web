package com.wutsi.blog.app.page.create

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Ignore
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class CreateControllerTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count-0.json")
        stub(HttpMethod.POST, "/v1/story/search", HttpStatus.OK, "v1/story/search-empty.json")
    }

    @Test
    fun `create blog for anonymous goto login`() {
        gotoPage(false)

        assertCurrentPageIs(PageName.LOGIN)
        assertElementVisible(".value-prop")
    }

    @Test
    @Ignore("flaky test")
    fun `create blog`() {
        gotoPage()

        assertCurrentPageIs(PageName.CREATE)

        input(".form-control", "wutsi")
        click("#btn-next")

        assertCurrentPageIs(PageName.CREATE_EMAIL)
        assertElementAttributeEndsWith("#btn-previous", "href", "/create")
        input(".form-control", "ray.sponsible1@gmail.com")

        givenUser(userId = 1, biography = "", blog = true)
        click("#btn-next")

        assertCurrentPageIs(PageName.BLOG)
        assertElementAttributeEndsWith(".next-action-biography .next-action a", "href", "/me/settings?highlight=biography-container#general")
        assertElementAttributeEndsWith(".next-action-newsletter .next-action a", "href", "/me/settings?highlight=newsletter-container#newsletter")
        assertElementAttributeEndsWith(".next-action-twitter .next-action a", "href", "/me/settings?highlight=channels-container#channels")
        assertElementVisible("#btn-create-story")

        click("#btn-create-story")
        assertCurrentPageIs(PageName.EDITOR)
    }

    private fun gotoPage(login: Boolean = true) {
        if (login) {
            login()
        }
        navigate("$url/create")
    }
}
