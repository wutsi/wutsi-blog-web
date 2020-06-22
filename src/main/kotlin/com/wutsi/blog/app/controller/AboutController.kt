package com.wutsi.blog.app.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AboutController{
    @Value("\${wutsi.about-url}")
    protected lateinit var url: String

    @GetMapping("/about")
    fun index(): String {
       return "redirect:$url"
    }
}
