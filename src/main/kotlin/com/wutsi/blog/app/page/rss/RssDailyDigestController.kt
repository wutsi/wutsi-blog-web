package com.wutsi.blog.app.page.rss

import com.wutsi.blog.app.page.rss.view.RssDailyDigestView
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RssDailyDigestController(
        private val view: RssDailyDigestView
){
    @GetMapping("/rss/digest/daily")
    fun index(): RssDailyDigestView {
        return view
    }
}
