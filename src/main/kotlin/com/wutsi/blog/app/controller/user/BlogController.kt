package com.wutsi.blog.app.controller.user

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BlogController(
        private val service: UserService,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.BLOG

    override fun shouldBeIndexedByBots() = true

    @GetMapping("/@/{name}")
    fun index(@PathVariable name: String, model: Model): String {
        val blog = service.get(name)
        val user = requestContext.currentUser()

        model.addAttribute("blog", blog)
        model.addAttribute("showWelcome", shouldShowWelcomeMessage(blog, user))
        model.addAttribute("showCreatePanel", shouldShowCreatePanel(blog, user))
        model.addAttribute("page", page(blog))
        return "page/user/blog"
    }

    private fun shouldShowWelcomeMessage(blog: UserModel, user: UserModel?): Boolean {
        if (user == null) {
            return false
        }

        return user.id == blog.id && blog.loginCount == 0L
    }

    private fun shouldShowCreatePanel(blog: UserModel, user: UserModel?): Boolean {
        if (user == null) {
            return false
        }

        return user.id == blog.id && blog.loginCount > 0L
    }

    protected fun page(user: UserModel) = PageModel(
                name = pageName(),
                title = user.fullName,
                description = if (user.biography == null) "" else user.biography,
                type = "profile",
                url = url(user),
                imageUrl = user.pictureUrl,
                baseUrl = baseUrl,
                assetUrl = assetUrl,
                robots = robots(),
                googleAnalyticsCode = this.googleAnalyticsCode
        )
}
