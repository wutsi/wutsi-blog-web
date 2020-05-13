package com.wutsi.blog.app.controller.story

import com.wutsi.blog.app.model.Permission
import com.wutsi.blog.app.model.PublishForm
import com.wutsi.blog.app.model.TopicModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.StoryService
import com.wutsi.blog.app.service.TopicService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class StoryTagController(
        private val topicService: TopicService,
        service: StoryService,
        requestContext: RequestContext
): AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.STORY_TAG

    override fun requiredPermissions() = listOf(Permission.editor)

    @GetMapping("/me/story/{id}/tag")
    fun index(
            @PathVariable id:Long,
            @RequestParam(required=false) error: String? = null,
            model: Model
    ): String {
        val story = getStory(id)

        val topics = topicService.all()
                .filter { it.parentId != -1L }
                .map {
                    val parent = topicService.get(it.parentId)
                    TopicModel(
                            id = it.id,
                            name = it.name,
                            displayName = if (parent == null) it.displayName else parent.displayName + " / " + it.displayName,
                            parentId = it.parentId
                    )
                }
        model.addAttribute("topics", topics)

        model.addAttribute("story", story)
        model.addAttribute("error", error)
        return "page/story/tag"
    }

    @GetMapping("/me/story/tag/submit")
    fun submit(@ModelAttribute editor: PublishForm): String {
        try {
            service.publish(editor)
            return "redirect:/me/story/published?pubid=${editor.id}"
        } catch (ex: Exception) {
            return "redirect:/me/story/${editor.id}/tag?error=publish_error"
        }
    }
}
