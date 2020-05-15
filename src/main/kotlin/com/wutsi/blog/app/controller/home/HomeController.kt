package com.wutsi.blog.app.controller.home

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class HomeController(
        private val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.HOME

    override fun shouldBeIndexedByBots() = true


    @GetMapping()
    fun index(model: Model): String {
        val stories = service.search(SearchStoryRequest(
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.published,
                limit = 50
        ))

        val main = mainStory(stories)
        val featured = featuredStories(stories, main)

        model.addAttribute("stories", stories)
        model.addAttribute("mainStory", main)
        model.addAttribute("featuredStories", featured)
        return "page/home/index"
    }

    fun mainStory(stories: List<StoryModel>) = stories
            .filter { it.thumbnailUrl != null && it.thumbnailUrl.isNotEmpty() }
            .firstOrNull()

    fun featuredStories(stories: List<StoryModel>, main: StoryModel?) = stories
            .filter{ it.id != main?.id}
}
