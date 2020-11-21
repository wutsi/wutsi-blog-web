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
    fun `create blog for anonymous goto login` () {
        gotoPage(false)

        assertCurrentPageIs(PageName.LOGIN)
        assertElementVisible(".value-prop")
    }

    @Test
    fun `create blog` () {
        gotoPage()

        assertCurrentPageIs(PageName.CREATE_NAME)

        input(".form-control", "wutsi")
        click("#btn-next")

        assertCurrentPageIs(PageName.CREATE_EMAIL)
        assertElementAttributeEndsWith("#btn-previous", "href", "/create")
        input(".form-control", "ray.sponsible1@gmail.com")
        click("#btn-next")

        assertCurrentPageIs(PageName.BLOG)
    }


    private fun gotoPage(login: Boolean = true) {
        if (login){
            login()
        }
        navigate("$url/create")
    }
}
