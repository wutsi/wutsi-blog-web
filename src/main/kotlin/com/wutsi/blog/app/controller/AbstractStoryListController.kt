package com.wutsi.blog.app.controller

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.client.story.StoryStatus
import org.slf4j.LoggerFactory
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

abstract class AbstractStoryListController(
        protected val service: StoryService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    protected abstract fun viewName(): String

    protected abstract fun stories(limit: Int, offset: Int): List<StoryModel>

    @GetMapping
    fun index(
            @RequestParam(defaultValue="20") limit:Int,
            @RequestParam(defaultValue="0") offset:Int,
            @RequestParam(required = false, name="pubid") publishedId: Long? = null,
            model: Model
    ): String {
        model.addAttribute("stories", stories(limit, offset))

        val totalDraftStories = service.count(StoryStatus.draft)
        val totalPublishedStories = service.count(StoryStatus.published)
        model.addAttribute("totalDraftStories", totalDraftStories)
        model.addAttribute("totalPublishedStories", totalPublishedStories)
        model.addAttribute("totalStories", totalDraftStories + totalPublishedStories)

        if (publishedId != null){
            loadPublishedStory(publishedId, model)
        }

        return viewName()
    }

    private fun loadPublishedStory(publishedId: Long, model: Model) {
        try {
            val published = service.get(publishedId)
            model.addAttribute("publishedStory", published)
        } catch (ex: Exception) {
            LoggerFactory.getLogger(javaClass).warn("Unable to load published story", ex)
        }
    }

}
