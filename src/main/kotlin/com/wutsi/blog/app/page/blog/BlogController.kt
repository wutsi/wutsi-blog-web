package com.wutsi.blog.app.page.blog

import com.wutsi.blog.app.common.controller.AbstractPageController
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.follower.service.FollowerService
import com.wutsi.blog.app.page.schemas.PersonSchemasGenerator
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BlogController(
        private val service: UserService,
        private val followerService: FollowerService,
        private val schemas: PersonSchemasGenerator,
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.BLOG

    override fun shouldBeIndexedByBots() = true

    @GetMapping("/@/{name}")
    fun index(@PathVariable name: String, model: Model): String {
        val blog = service.get(name)
        return blog(blog, model)
    }

    private fun blog(blog: UserModel, model: Model): String {
        val user = requestContext.currentUser()

        model.addAttribute("blog", blog)
        model.addAttribute("showCreatePanel", shouldShowCreatePanel(blog, user))
        model.addAttribute("page", getPage(blog))
        model.addAttribute("followerId", getFollowerId(blog))
        return "page/blog/index"
    }

    private fun getFollowerId(blog: UserModel): Long? =
        if (!requestContext.toggles().follow)  null else followerService.findFollwerId(blog.id)


    private fun shouldShowCreatePanel(blog: UserModel, user: UserModel?) = user?.id == blog.id

    protected fun getPage(user: UserModel) = createPage(
            name = pageName(),
            title = user.fullName,
            description = if (user.biography == null) "" else user.biography,
            type = "profile",
            url = url(user),
            imageUrl = user.pictureUrl,
            schemas = schemas.generate(user)
    )
}
