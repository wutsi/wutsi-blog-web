package com.wutsi.blog.app.page.sitemap

import com.wutsi.blog.app.page.sitemap.view.SitemapView
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SitemapController(
    private val view: SitemapView
) {

    @ResponseBody
    @GetMapping("/sitemap.xml")
    fun index(): SitemapView = view
}
