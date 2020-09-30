package com.wutsi.blog.app.page.blog

import com.wutsi.blog.app.page.blog.service.BlogService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/blog")
class PreferredBlogController(private val service: BlogService) {

    @GetMapping("/preferred")
    fun index(model: Model): String {
        val blogs = service.preferred()
        model.addAttribute("blogs", blogs)

        return "page/blog/preferred"
    }
}
