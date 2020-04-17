package com.wutsi.blog.app.controller.user

import com.wutsi.blog.app.controller.AbstractPageController
import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MeController(
        requestContext: RequestContext
): AbstractPageController(requestContext) {
    override fun pageName() = PageName.ME

    override fun shouldBeIndexedByBots() = true

    @GetMapping("/me")
    fun index(model: Model): String {

        return "page/user/me"
    }


    override fun page(): PageModel {
        val user = requestContext.currentUser()!!
        return PageModel(
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
}
