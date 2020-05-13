package com.wutsi.blog.app.controller.stats

import com.wutsi.blog.app.controller.story.AbstractStoryController
import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.toastui.BarChartModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StatsService
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.stats.StatsType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping()
class StatsStoryController(
        private val stats: StatsService,
        service: StoryService,
        requestContext: RequestContext
): AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.STATS_STORY

    override fun requiredPermissions() = listOf(Permission.owner)

    @GetMapping("/stats/story/{id}")
    fun index(@PathVariable id: Long, model: Model): String {
        val story = getStory(id)
        model.addAttribute("story", story)

        val statistics = stats.story(story)
        model.addAttribute("stats", statistics)

        return "page/stats/story"
    }

    @ResponseBody
    @GetMapping(value=["/stats/story/{id}/bar-chart-data"], produces = ["application/json"])
    fun barChartData(
            @PathVariable id: Long,
            @RequestParam type: StatsType
    ): BarChartModel {
        val story = getStory(id)
        return stats.barChartData(story, type)
    }
}
