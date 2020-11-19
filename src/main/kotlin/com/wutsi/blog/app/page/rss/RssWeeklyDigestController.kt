package com.wutsi.blog.app.page.rss

import com.wutsi.blog.app.page.rss.view.RssWeeklyDigestView
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Deprecated("")
@RestController
class RssWeeklyDigestController(
        private val view: RssWeeklyDigestView
){
    @GetMapping("/rss/digest/weekly")
    fun index(): RssWeeklyDigestView {
        return view
    }
}
