package com.wutsi.blog.app.controller.story

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.core.logging.KVLogger
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder

@Controller
@RequestMapping("/me/syndicate")
class StorySyndicateController(
        private val service: StoryService,
        private val logger: KVLogger,
        requestContext: RequestContext
): AbstractPageController( requestContext) {
    override fun pageName() = PageName.STORY_SYNDICATE

    @GetMapping
    fun index(
            @RequestParam(required = false) error: String?=null,
            model: Model
    ): String {
        model.addAttribute("error", error)
        return "page/story/syndicate"
    }

    @GetMapping("/import")
    fun import(@RequestParam url: String): String {
        try {
            val id = service.import(url)
            return "redirect:/editor/$id"
        } catch (ex: Exception) {
            logger.add("Exception", ex.javaClass.name)
            logger.add("ExceptionMessage", ex.message)
            return "redirect:/me/syndicate?error=" + URLEncoder.encode(ex.message, "utf-8")
        }
    }
}
