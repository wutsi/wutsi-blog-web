package com.wutsi.blog.app.page.calendar

import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.calendar.service.PostService
import com.wutsi.blog.app.security.model.Permission
import com.wutsi.blog.app.util.PageName
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/me/calendar/post")
@ConditionalOnProperty(value = ["wutsi.toggles.post"], havingValue = "true")
class ShowPostController(
    postService: PostService,
    requestContext: RequestContext
) : AbstractPostController(postService, requestContext) {
    override fun pageName() = PageName.CALENDAR_EDIT

    override fun requiredPermissions(): List<Permission> = listOf(Permission.owner)

    @GetMapping()
    fun index(@RequestParam id: Long, model: Model): String {
        val post = getPost(id)
        model.addAttribute("post", post)
        return "page/calendar/post"
    }

    @GetMapping("/delete")
    fun delete(@RequestParam id: Long): String {
        val post = getPost(id)  // Get the post to ensure you have access
        postService.delete(post.id)
        return "redirect:/me/calendar"
    }

    @GetMapping("/picture")
    fun picture(@RequestParam id: Long, @RequestParam url: String): String {
        val post = getPost(id)  // Get the post to ensure you have access
        postService.setPicture(post.id, url)
        return "redirect:/me/calendar/post?id=$id"
    }
}
