package com.wutsi.blog.app.controller

import com.wutsi.blog.app.model.TagListModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TagService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/tag")
class TagController(
        private val service: TagService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun page() = PageName.STORY_DRAFT

    @ResponseBody()
    @GetMapping("/search", produces = ["application/json"])
    fun search(@RequestParam(name="q") query: String): TagListModel {
        return service.search(query)
    }
}
