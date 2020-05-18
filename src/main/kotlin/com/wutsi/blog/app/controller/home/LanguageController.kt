package com.wutsi.blog.app.controller.home

import com.wutsi.blog.app.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/language")
class LanguageController(
        private val users: UserService
) {
    @GetMapping("/read-all")
    fun index(model: Model): String {
        var readAll = users.canReadStoriesInAllLanguages()
        model.addAttribute("show", readAll == null)

        return "page/language/read-all"
    }

    @ResponseBody
    @GetMapping("/read-all/set", produces = ["text/html"])
    fun set(@RequestParam value: String): String {
        users.setReadStoriesInAllLanguages(value.toBoolean())
        return "OK"
    }

}
