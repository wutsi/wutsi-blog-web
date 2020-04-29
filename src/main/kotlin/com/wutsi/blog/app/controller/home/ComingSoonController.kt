package com.wutsi.blog.app.controller.home

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
@ConditionalOnProperty(name = ["wutsi.toggles.coming-soon"], havingValue = "true")
class ComingSoonController(
        service: StoryService,
        requestContext: RequestContext
): HomeController(service, requestContext) {
    @GetMapping("/")
    override fun index(model: Model): String {
        return "page/home/coming-soon"
    }

    @GetMapping("/coming-soon/subscribed")
    fun subscribed(model: Model): String {
        model.addAttribute("subscribed", true)
        return "page/home/coming-soon"
    }
}
