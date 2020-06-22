package com.wutsi.blog.app.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class TermsAndConditionController{
    @Value("\${wutsi.terms-conditions-url}")
    protected lateinit var url: String

    @GetMapping("/terms")
    fun index(): String {
       return "redirect:$url"
    }
}
