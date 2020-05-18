package com.wutsi.blog.app.controller.home

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.ViewService
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
        private val storyService: StoryService,
        private val viewService: ViewService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.HOME

    override fun shouldBeIndexedByBots() = true

    @GetMapping()
    fun index(model: Model): String {
        val stories = storyService.search(SearchStoryRequest(
                status = StoryStatus.published,
                language = storyService.searchLanguage(),
                live = true,
                sortBy = StorySortStrategy.published,
                limit = 50
        ))

        val viewedIds = viewService.search(stories.map { it.id })
        val main = mainStory(stories, viewedIds)
        val featured = featuredStories(stories, viewedIds, main)

        model.addAttribute("stories", stories)
        model.addAttribute("mainStory", main)
        model.addAttribute("featuredStories", bubbleUpNonViewedStories(featured, viewedIds))
        return "page/home/index"
    }

    private fun mainStory(stories: List<StoryModel>, viewedIds: List<Long>): StoryModel? {
        if (stories.isEmpty()){
            return null
        }

        val main = stories
                .filter { !viewedIds.contains(it.id) }
                .filter { it.thumbnailUrl != null && it.thumbnailUrl.isNotEmpty() }
                .firstOrNull()

        return if (main == null) stories[0] else main
    }

    private fun featuredStories(
            stories: List<StoryModel>,
            viewedIds: List<Long>,
            main: StoryModel?
    ): List<StoryModel> {

        val result = LinkedHashMap<UserModel, StoryModel>()
        bubbleUpNonViewedStories(stories, viewedIds)
                .filter{ it.id != main?.id }
                .forEach{
                    val user = it.user
                    if (!result.containsKey(user) && user.id != main?.user?.id){
                        result[user] = it
                    }
                }

        return result.values.toList()
    }

    private fun bubbleUpNonViewedStories(stories: List<StoryModel>, viewedIds: List<Long>): List<StoryModel> {
        if (stories.size <= 1 || viewedIds.isEmpty()){
            return stories
        }

        val result = mutableListOf<StoryModel>()

        // Add non viewed stories
        stories
                .filter { !viewedIds.contains(it.id) }
                .forEach { result.add(it) }

        // Add viewed stories
        stories
                .filter { viewedIds.contains(it.id) }
                .forEach { result.add(it) }

        return result
    }
}
