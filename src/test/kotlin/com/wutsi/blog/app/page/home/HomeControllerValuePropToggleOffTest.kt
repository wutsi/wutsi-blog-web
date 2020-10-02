package com.wutsi.blog.app.page.home

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource


@TestPropertySource(
        properties = [
            "wutsi.toggles.value-prop=false"
        ]
)
class HomeControllerValuePropToggleOffTest: SeleniumTestSupport() {
    @Test
    fun `value prop not showing for anonymous user`() {
        driver.get(url)

        assertCurrentPageIs(PageName.HOME)
        assertElementCount("#value-prop", 0)
    }

    @Test
    fun `value prop not showing for logged user`() {
        login()

        assertCurrentPageIs(PageName.HOME)
        assertElementCount("#value-prop", 0)
    }
}
