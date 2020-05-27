package com.wutsi.blog.app.controller.home

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping()
class PWAController(
        @Value("\${wutsi.base-url}") private val baseUrl: String
) {
    @GetMapping("/sw.js", produces = ["text/javascript"])
    fun sw(): String {
        return "page/home/sw.js"
    }
}
