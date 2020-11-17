package com.wutsi.blog.app.page.home

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Assert.*
import org.junit.Test

class WritersControllerTest: SeleniumTestSupport() {
    @Test
    fun `open writer page`(){
        gotoPage()

        assertCurrentPageIs(PageName.WRITERS)
        assertElementCount(".writer", 5)
    }

    fun gotoPage(){
        driver.get(url)
        click(".btn-writers")
    }
}
