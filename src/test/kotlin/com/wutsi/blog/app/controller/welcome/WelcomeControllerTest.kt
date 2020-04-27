package com.wutsi.blog.app.controller.welcome

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class WelcomeControllerTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user1-first-login.json")
        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1-first-login.json")
        stub(HttpMethod.POST, "/v1/user/1", HttpStatus.OK)
    }

    @Test
    fun `welcome existing user` () {
        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user1.json")
        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")

        gotoPage()

        assertCurrentPageIs(PageName.BLOG)
    }


    @Test
    fun `set name` () {
        gotoPage()
        assertCurrentPageIs(PageName.WELCOME)

        input(".form-control", "ray.sponsible1")
        click("#btn-next")

        assertCurrentPageIs(PageName.WELCOME_FULLNAME)
    }

    @Test
    fun `set name empty` () {
        gotoPage()

        input(".form-control", "")
        click("#btn-next")

        assertCurrentPageIs(PageName.WELCOME)
    }

    @Test
    fun `set fullname` () {
        gotoPage()

        click("#btn-next")

        input(".form-control", "Ray S.")
        click("#btn-next")

        assertCurrentPageIs(PageName.WELCOME_EMAIL)
    }

    @Test
    fun `set fullname empty` () {
        gotoPage()

        click("#btn-next")

        input(".form-control", "")
        click("#btn-next")

        assertCurrentPageIs(PageName.WELCOME_EMAIL)
    }

    @Test
    fun `set fullname back` () {
        gotoPage()

        click("#btn-next")

        click("#btn-previous")

        assertCurrentPageIs(PageName.WELCOME)
    }



    @Test
    fun `set email` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")

        input(".form-control", "ray.sponsible1@gmail.com")
        click("#btn-next")

        assertCurrentPageIs(PageName.WELCOME_BIOGRAPHY)
    }

    @Test
    fun `set email back` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")

        click("#btn-previous")

        assertCurrentPageIs(PageName.WELCOME_FULLNAME)
    }

    @Test
    fun `set biography` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")
        click("#btn-next")

        input(".form-control", "This is a nice bio")
        click("#btn-next")

        assertCurrentPageIs(PageName.WELCOME_PICTURE)
    }

    @Test
    fun `set biography back` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")
        click("#btn-next")

        click("#btn-previous")

        assertCurrentPageIs(PageName.WELCOME_EMAIL)
    }

    @Test
    fun `set picture` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")
        click("#btn-next")
        click("#btn-next")
        click("#btn-next")

        assertCurrentPageIs(PageName.WELCOME_SUCCESS)
    }

    @Test
    fun `set picture back` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")
        click("#btn-next")
        click("#btn-next")

        click("#btn-previous")

        assertCurrentPageIs(PageName.WELCOME_BIOGRAPHY)
    }

    @Test
    fun `success and create story` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")
        click("#btn-next")
        click("#btn-next")
        click("#btn-next")

        click("#btn-create-story")
        assertCurrentPageIs(PageName.STORY_DRAFT)
    }

    @Test
    fun `success and open blog` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")
        click("#btn-next")
        click("#btn-next")
        click("#btn-next")

        click("#btn-my-blog")
        assertCurrentPageIs(PageName.BLOG)
    }

    private fun gotoPage() {
        login()
        navigate("$url/welcome")

        assertElementNotPresent(".label-danger")
    }
}
