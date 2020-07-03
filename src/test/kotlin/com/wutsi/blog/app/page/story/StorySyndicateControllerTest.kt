package com.wutsi.blog.app.page.story

import com.wutsi.blog.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class StorySyndicateControllerTest : SeleniumTestSupport() {
    var i: Int = 0

    override fun setupWiremock() {
        super.setupWiremock()

        stub(HttpMethod.GET, "/v1/story/100", HttpStatus.OK, "v1/story/get-story100-draft.json")
        stub(HttpMethod.POST, "/v1/story/import", HttpStatus.OK, "v1/story/import.json")
        stub(HttpMethod.POST, "/v1/story/count", HttpStatus.OK, "v1/story/count.json")
    }

    @Test
    fun `import story`() {
        gotoPage()

        input("#url", "https://kamerkongossa.cm/2020/01/07/a-yaounde-on-rencontre-le-sous-developpement-par-les-chemins-quon-emprunte-pour-leviter/")
        click("#btn-submit")

        assertCurrentPageIs(PageName.EDITOR)
        Thread.sleep(1000)  // Wait for data to be fetched

        assertElementAttribute("#title", "value", "Lorem Ipsum #100")
    }

    @Test
    fun `import story failed`() {
        stub(HttpMethod.POST, "/v1/story/import", HttpStatus.NOT_FOUND)

        gotoPage()

        input("#url", "https://invalid-url.com/xxx/fldkfld")
        click("#btn-submit")

        assertCurrentPageIs(PageName.STORY_SYNDICATE)
        assertElementPresent(".alert-danger")
    }

    fun gotoPage() {
        login()
        click("nav .nav-item")
        if (i++ % 2 == 0) {
            click("#navbar-draft")
            click("#toolbar-syndicate")
        } else {
            click("#navbar-syndicate")
        }

        assertCurrentPageIs(PageName.STORY_SYNDICATE)
    }

}
