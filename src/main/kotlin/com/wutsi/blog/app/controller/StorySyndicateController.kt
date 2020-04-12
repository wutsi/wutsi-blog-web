package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/story/syndicate")
class StorySyndicateController(
        requestContext: RequestContext
): AbstractPageController( requestContext) {
    override fun pageName() = PageName.STORY_SYNDICATE

    fun index(): String {
        return "page/story/syndicate"
    }
}
