package com.wutsi.blog.app.page.legal

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
