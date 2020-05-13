package com.wutsi.blog.app.controller

import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.core.exception.ConflictException
import com.wutsi.core.exception.ForbiddenException
import com.wutsi.core.exception.NotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ModelAttribute
import java.util.UUID

abstract class AbstractPageController(
        protected val requestContext: RequestContext
) {
    @Value("\${wutsi.asset-url}")
    protected lateinit var assetUrl: String

    @Value("\${wutsi.base-url}")
    protected lateinit var baseUrl: String

    @Value("\${wutsi.google.ga.code}")
    protected lateinit var googleAnalyticsCode: String


    protected abstract fun pageName(): String

    @ModelAttribute(ModelAttributeName.USER)
    fun getUser() = requestContext.currentUser()

    @ModelAttribute(ModelAttributeName.TOGGLES)
    fun getToggles() = requestContext.toggles()

    @ModelAttribute(ModelAttributeName.PAGE)
    fun getPage() = page()

    @ModelAttribute(ModelAttributeName.HITID)
    fun getHitId() = UUID.randomUUID().toString()

    open fun shouldBeIndexedByBots() = false

    protected fun robots () = if (shouldBeIndexedByBots()) "all" else "noindex,nofollow"

    open fun page() = PageModel(
            name = pageName(),
            title = requestContext.getMessage("wutsi.title"),
            description = requestContext.getMessage("wutsi.description"),
            type = "website",
            robots = robots(),
            baseUrl = baseUrl,
            assetUrl = assetUrl,
            googleAnalyticsCode = this.googleAnalyticsCode
    )

    protected fun url(story: StoryModel) = baseUrl + story.slug

    protected fun url(user: UserModel) = baseUrl + user.slug

    protected fun checkOwnership(story: StoryModel) {
        val user = requestContext.currentUser()
        if (user == null || story.user.id != user.id) {
            throw ForbiddenException("ownership_error")
        }
    }

    protected fun checkPublished(story: StoryModel, live: Boolean=true) {
        if (!story.published) {
            throw NotFoundException("story_not_published")
        }
        if (live && !story.live) {
            throw NotFoundException("story_not_live")
        }
    }

    protected fun errorKey(ex: Exception): String {
        if (ex is ConflictException){
            val message = ex.message
            if (message == "duplicate_name" || message == "duplicate_email"){
                return "error.$message"
            }
        }
        return "error.unexpected"
    }
}
