package com.wutsi.blog.app.page.story

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType.preferred_author
import com.wutsi.blog.client.story.StorySortStrategy.published
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class CarouselController(
    private val storyService: StoryService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName(): String = PageName.STORY_CAROUSEL;

    @GetMapping("/story/carousel")
    fun recommend(
        @RequestParam(required = false) topicId: Long? = null,
        @RequestParam(required = false) title: String? = null,
        model: Model
    ): String {
        val stories = storyService.search(
            SearchStoryRequest(
                topicId = topicId,
                sortBy = published,
                limit = 20
            )
        )
        val xstories = storyService.sort(
            stories = stories,
            bubbleDownViewedStories = true,
            algorithm = preferred_author
        )

        model.addAttribute("title", title)
        model.addAttribute("stories", xstories.take(3))
        return "page/story/carousel"
    }
}
