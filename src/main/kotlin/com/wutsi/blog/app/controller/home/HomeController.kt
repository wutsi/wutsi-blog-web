package com.wutsi.blog.app.controller.home

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType
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
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.HOME

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    @GetMapping()
    fun index(model: Model): String {
        loadStories(model)
        return "page/home/index"
    }

    private fun loadStories(model: Model) {
        var stories = storyService.search(SearchStoryRequest(
                status = StoryStatus.published,
                language = storyService.searchLanguage(),
                live = true,
                sortBy = StorySortStrategy.published,
                limit = 50
        ))

        stories = storyService.sort(stories, SortAlgorithmType.most_recent)
        val main = mainStory(stories)
        val featured = featuredStories(stories, main)
        val authors = featuredAuthors(stories)

        model.addAttribute("stories", stories)
        model.addAttribute("mainStory", main)
        model.addAttribute("featuredStories", featured)
        model.addAttribute("authors", authors)
    }

    private fun mainStory(stories: List<StoryModel>): StoryModel? {
        if (stories.isEmpty()){
            return null
        }

        val main = stories
                .filter { it.thumbnailUrl != null && it.thumbnailUrl.isNotEmpty() }
                .firstOrNull()

        return if (main == null) stories[0] else main
    }

    private fun featuredStories(
            stories: List<StoryModel>,
            main: StoryModel?
    ): List<StoryModel> {

        val result = LinkedHashMap<UserModel, StoryModel>()
        stories
                .filter{ it.id != main?.id }
                .forEach{
                    val user = it.user
                    if (!result.containsKey(user) && user.id != main?.user?.id){
                        result[user] = it
                    }
                }

        return result.values
                .toList()
                .sortedByDescending { it.publishedDateTime }
    }

    private fun featuredAuthors(stories: List<StoryModel>): List<UserModel> = stories
            .map { it.user }
            .toSet()
            .take(5)
}
