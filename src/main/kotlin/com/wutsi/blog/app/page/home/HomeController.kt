package com.wutsi.blog.app.page.home

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.schemas.WutsiSchemasGenerator
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
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
        if (requestContext.toggles().valueProp && requestContext.currentUser() == null){
            return "page/home/index"
        } else {
            loadStories(model)
            return "page/home/wall"
        }
    }

    private fun loadStories(model: Model) {
        val wall = storyService.searchWallStories()

        model.addAttribute("stories", wall.stories)
        model.addAttribute("mainStory", wall.mainStory)
        model.addAttribute("featuredStories", wall.featureStories)
        model.addAttribute("popularStories", wall.popularStories)
    }
}
