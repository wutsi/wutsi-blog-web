package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType.no_sort
import com.wutsi.blog.client.story.StorySortStrategy.published
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class StoryCarouselController(
    private val storyService: StoryService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName(): String = PageName.STORY_CAROUSEL

    @GetMapping("/story/carousel")
    fun recommend(
        @RequestParam(required = false, defaultValue = "-1") topicId: Long = -1,
        @RequestParam(required = false) title: String? = null,
        model: Model
    ): String {
        val stories = storyService.sort(
            stories = storyService.search(
                SearchStoryRequest(
                    topicId = if (topicId > -1) topicId else null,
                    sortBy = published,
                    limit = 20
                )
            ),
            bubbleDownViewedStories = true,
            algorithm = no_sort
        ).filter { it.thumbnailUrl.isNullOrBlank() }

        model.addAttribute("title", title)
        model.addAttribute("stories", stories.take(3))
        return "page/story/carousel"
    }
}
