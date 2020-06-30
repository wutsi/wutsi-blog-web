package com.wutsi.blog.app.page.create

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


class CreateControllerTest : SeleniumTestSupport() {
    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user1-first-login.json")
        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1-first-login.json")
        stub(HttpMethod.POST, "/v1/user/1", HttpStatus.OK)
    }


    @Test
    fun `create` () {
        gotoPage(false)
        assertCurrentPageIs(PageName.CREATE)

        click("#btn-create")
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun `existing user` () {
        stub(HttpMethod.GET, "/v1/user/1", HttpStatus.OK, "v1/user/get-user1.json")
        stub(HttpMethod.GET, "/v1/user/@/ray.sponsible", HttpStatus.OK, "v1/user/get-user1.json")

        gotoPage()

        assertCurrentPageIs(PageName.BLOG)
    }

    @Test
    fun `set name` () {
        gotoPage()

        assertCurrentPageIs(PageName.CREATE_NAME)

        input(".form-control", "ray.sponsible1")
        click("#btn-next")

        assertCurrentPageIs(PageName.CREATE_FULLNAME)
    }

    @Test
    fun `set name empty` () {
        gotoPage()

        input(".form-control", "")
        click("#btn-next")

        assertCurrentPageIs(PageName.CREATE_NAME)
    }

    @Test
    fun `set fullname` () {
        gotoPage()

        click("#btn-next")

        input(".form-control", "Ray S.")
        click("#btn-next")

        assertCurrentPageIs(PageName.CREATE_EMAIL)
    }

    @Test
    fun `set fullname empty` () {
        gotoPage()

        click("#btn-next")

        input(".form-control", "")
        click("#btn-next")

        assertCurrentPageIs(PageName.CREATE_EMAIL)
    }

    @Test
    fun `set fullname back` () {
        gotoPage()

        click("#btn-next")

        click("#btn-previous")

        assertCurrentPageIs(PageName.CREATE_NAME)
    }



    @Test
    fun `set email` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")

        input(".form-control", "ray.sponsible1@gmail.com")
        click("#btn-next")

        assertCurrentPageIs(PageName.CREATE_BIOGRAPHY)
    }

    @Test
    fun `set email back` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")

        click("#btn-previous")

        assertCurrentPageIs(PageName.CREATE_FULLNAME)
    }

    @Test
    fun `set biography` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")
        click("#btn-next")

        input(".form-control", "This is a nice bio")
        click("#btn-next")

        assertCurrentPageIs(PageName.CREATE_PICTURE)
    }

    @Test
    fun `set biography back` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")
        click("#btn-next")

        click("#btn-previous")

        assertCurrentPageIs(PageName.CREATE_EMAIL)
    }

    @Test
    fun `set picture` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")
        click("#btn-next")
        click("#btn-next")
        click("#btn-next")

        assertCurrentPageIs(PageName.CREATE_SUCCESS)
    }

    @Test
    fun `set picture back` () {
        gotoPage()

        click("#btn-next")
        click("#btn-next")
        click("#btn-next")
        click("#btn-next")

        click("#btn-previous")

        assertCurrentPageIs(PageName.CREATE_BIOGRAPHY)
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

    private fun gotoPage(login: Boolean = true) {
        if (login){
            login()
            navigate("$url/create/name")
        } else {
            navigate("$url/create")
        }
    }
}
