package com.wutsi.blog.app.page.home

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SortAlgorithmType
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class HomeController(
        private val storyService: StoryService,
        private val schemas: WutsiSchemasGenerator,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(HomeController::class.java)
    }

    override fun pageName() = PageName.HOME

    override fun shouldBeIndexedByBots() = true

    override fun shouldShowGoogleOneTap() = true

    override fun page() = createPage(
            title = requestContext.getMessage("wutsi.title"),
            description = requestContext.getMessage("wutsi.description"),
            schemas = schemas.generate(),
            showNotificationOptIn = true
    )

    @GetMapping()
    fun index(model: Model): String {
//        if (requestContext.currentUser() == null){
//            return "page/home/index"
//        } else {
        loadStories(model)
        return "page/home/wall"
//        }
    }

    private fun loadStories(model: Model) {
        val stories = findRecentStories()
        if (stories.isEmpty()) {
            return
        }

        val mainStory = mainStory(stories)
        val featuredStories = findFeaturedStories(stories, mainStory)
        val popularStories = findPopularStories(stories, featuredStories, mainStory)

        model.addAttribute("stories", stories)
        model.addAttribute("mainStory", mainStory)
        model.addAttribute("featuredStories", featuredStories)
        model.addAttribute("popularStories", popularStories)
    }

    private fun findRecentStories(): List<StoryModel> {
        val stories = storyService.search(SearchStoryRequest(
                status = StoryStatus.published,
                live = true,
                sortBy = StorySortStrategy.published,
                limit = 50
        ))
        return sort(stories)
    }

    private fun sort(stories: List<StoryModel>): List<StoryModel> {
        try{
            return storyService.sort(
                    stories = stories,
                    algorithm = SortAlgorithmType.most_recent,
                    statsHoursOffset = 24*1, // 1 days
                    bubbleDownViewedStories = true
            )
        }catch(ex: Exception){
            LOGGER.warn("Unable to sort stories", ex)
            return stories
        }
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

    private fun findFeaturedStories(
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

    private fun findPopularStories(
            stories: List<StoryModel>,
            featuredStories: List<StoryModel>,
            main: StoryModel?
    ): List<StoryModel> {
        val result = storyService.sort(
                stories = stories,
                algorithm = SortAlgorithmType.most_viewed,
                statsHoursOffset = 24*7, // 7 days
                bubbleDownViewedStories = false
        )

        val featuredIds = featuredStories.map { it.id }
        return result
                .filter {  main?.id != it.id && !featuredIds.contains(it.id) }
                .take(5)
    }
}
