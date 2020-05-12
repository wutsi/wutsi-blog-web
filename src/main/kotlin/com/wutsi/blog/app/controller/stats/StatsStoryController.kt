package com.wutsi.blog.app.controller.stats

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StatsService
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping()
class StatsStoryController(
        private val stories: StoryService,
        private val stats: StatsService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.STATS_STORY

    @GetMapping("/stats/story/{id}")
    fun index(@PathVariable id: Long, model: Model): String {
        val story = stories.get(id)
        checkOwnership(story)
        model.addAttribute("story", story)

        val statistics = stats.story(story)
        model.addAttribute("stats", statistics)

        return "page/stats/story"
    }
}
