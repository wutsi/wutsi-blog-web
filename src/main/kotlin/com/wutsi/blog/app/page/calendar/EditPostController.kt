package com.wutsi.blog.app.page.calendar

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.calendar.model.UpdatePostForm
import com.wutsi.blog.app.page.calendar.service.PostService
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.blog.app.util.PageName
import com.wutsi.core.util.DateUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.text.SimpleDateFormat
import java.util.Date

@Controller
@RequestMapping("/me/calendar/edit")
@ConditionalOnProperty(value = ["wutsi.toggles.post"], havingValue = "true")
class EditPostController(
    postService: PostService,
    requestContext: RequestContext
) : AbstractPostController(postService, requestContext) {
    override fun pageName() = PageName.CALENDAR_EDIT

    override fun requiredPermissions(): List<Permission> = listOf(Permission.owner)

    @GetMapping()
    fun index(@RequestParam id: Long, model: Model): String {
        val post = getPost(id)
        model.addAttribute("story", post.story)
        model.addAttribute("channel", post.channel)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val request = UpdatePostForm(
            id = post.id,
            message = post.message?.let { it } ?: "",
            scheduledDateTime = fmt.format(post.scheduledPostDateTime)
        )
        model.addAttribute("post", request)
        model.addAttribute("minDate", fmt.format(DateUtils.addDays(Date(), 1)))
        return "page/calendar/edit"
    }

    @PostMapping
    fun submit(@ModelAttribute form: UpdatePostForm): String {
        postService.update(form)
        return "redirect:/me/calendar/post?id=${form.id}"
    }
}
