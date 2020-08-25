package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.service.RecommendationService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class RecommendationController(
        private val recommendations: RecommendationService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {

    override fun pageName() = PageName.RECOMMEND

    @GetMapping("/recommend")
    fun recommend(
            @RequestParam storyId: Long,
            @RequestParam(required = false, defaultValue = "summary") layout: String = "summary",
            model: Model
    ): String {
        val stories = recommendations
                .search(storyId)
                .take(3)

        model.addAttribute("layout", layout)
        model.addAttribute("stories", stories)
        return "page/story/recommend"
    }
}
