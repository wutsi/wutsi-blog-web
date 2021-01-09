package com.wutsi.blog.app.page.editor.service.filter

import junit.framework.Assert.assertEquals
import org.jsoup.Jsoup
import org.junit.Test

class ButtonFilterTest {
    private val filter = ButtonFilter()

    @Test
    fun filterExternal() {
        val doc = Jsoup.parse(
            "<body>" +
                "<div class='button'><a href='http://www.google.ca'>Yo</a></div>" +
                "<div><a href='http://www.yahoo.ca'>Yo</a></div>" +
                "<a href='http://www.msn.ca'>Yo</a>"
        )
        filter.filter(doc)

        assertEquals(
            "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <div class=\"button\">\n" +
                "   <a href=\"http://www.google.ca\" class=\"btn btn-primary\">Yo</a>\n" +
                "  </div>\n" +
                "  <div>\n" +
                "   <a href=\"http://www.yahoo.ca\">Yo</a>\n" +
                "  </div><a href=\"http://www.msn.ca\">Yo</a>\n" +
                " </body>\n" +
                "</html>",
            doc.html()
        )
    }
}
