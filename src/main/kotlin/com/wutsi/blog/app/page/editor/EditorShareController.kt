package com.wutsi.blog.app.page.editor

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.app.page.story.AbstractStoryController
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.channel.ChannelType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class EditorShareController(
        private val channels: ChannelService,
        service: StoryService,
        requestContext: RequestContext,

        @Value("\${wutsi.base-url}") private val websiteUrl: String
): AbstractStoryController(service, requestContext) {
    override fun pageName() = PageName.EDITOR_SHARE

    override fun requiredPermissions() = listOf(Permission.editor)

    @GetMapping("/me/story/{id}/share")
    fun index(
            @PathVariable id:Long,
            model: Model
    ): String {
        val story = getStory(id)

        model.addAttribute("story", story)
        model.addAttribute("storyUrl", "${websiteUrl}${story.slug}")
        loadChannels(model)
        return "page/editor/share"
    }

    fun loadChannels(model: Model) {
        try {
            val all = channels.all()
            model.addAttribute("channelTwitter", all.find { it.type == ChannelType.twitter && it.connected })
            model.addAttribute("channelFacebook", all.find { it.type == ChannelType.facebook && it.connected })
            model.addAttribute("channelLinkedin", all.find { it.type == ChannelType.linkedin && !it.connected })
        } catch (ex: Exception) {

        }
    }
}
