package com.wutsi.blog.app.page.calendar

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.calendar.model.CreatePostForm
import com.wutsi.blog.app.page.calendar.service.PostService
import com.wutsi.blog.app.page.channel.service.ChannelService
import com.wutsi.blog.app.page.story.service.StoryService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.client.SortOrder.descending
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy.created
import com.wutsi.core.util.DateUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.text.SimpleDateFormat
import java.util.Date

@Controller
@RequestMapping("/me/calendar/create")
@ConditionalOnProperty(value = ["wutsi.toggles.post"], havingValue = "true")
class CreatePostController(
    private val postService: PostService,
    private val storyService: StoryService,
    private val channelService: ChannelService,
    requestContext: RequestContext
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.CALENDAR_CREATE

    @GetMapping
    fun index(model: Model): String {
        val stories = storyService.search(
            SearchStoryRequest(
                userIds = listOf(requestContext.currentUser()!!.id),
                sortBy = created,
                sortOrder = descending,
                limit = 365
            )
        ).filter { it.published || it.scheduledPublishDateTimeAsDate != null }
        model.addAttribute("stories", stories)

        val channels = channelService.all()
        model.addAttribute("channels", channels)

        model.addAttribute("post", CreatePostForm())
        model.addAttribute("minDate", SimpleDateFormat("yyyy-MM-dd").format(DateUtils.addDays(Date(), 1)))
        return "page/calendar/create"
    }

    @PostMapping
    fun submit(@ModelAttribute form: CreatePostForm): String {
        val id = postService.create(form)
        return "redirect:/me/calendar/post?id=$id"
    }
}
