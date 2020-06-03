package com.wutsi.blog.app.controller

import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.core.exception.ConflictException
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ModelAttribute
import java.util.UUID

abstract class AbstractPageController(
        @ModelAttribute(ModelAttributeName.REQUEST_CONTEXT) protected val requestContext: RequestContext
) {
    @Value("\${wutsi.asset-url}")
    protected lateinit var assetUrl: String

    @Value("\${wutsi.base-url}")
    protected lateinit var baseUrl: String

    @Value("\${wutsi.google.ga.code}")
    protected lateinit var googleAnalyticsCode: String

    @Value("\${wutsi.facebook.pixel.code}")
    protected lateinit var facebookPixelId: String

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

    open fun shouldShowGoogleOneTap() = false

    protected fun getPageRobotsHeader () = if (shouldBeIndexedByBots()) "all" else "noindex,nofollow"

    protected fun getPageGoogleOneTap() = shouldShowGoogleOneTap()
            && requestContext.toggles().googleOneTapSignIn
            && requestContext.accessToken() == null

    open fun page() = PageModel(
            name = pageName(),
            title = requestContext.getMessage("wutsi.title"),
            description = requestContext.getMessage("wutsi.description"),
            type = "website",
            robots = getPageRobotsHeader(),
            baseUrl = baseUrl,
            assetUrl = assetUrl,
            googleAnalyticsCode = this.googleAnalyticsCode,
            facebookPixelCode = this.facebookPixelId,
            showGoogleOneTap = getPageGoogleOneTap()
    )

    protected fun url(story: StoryModel) = baseUrl + story.slug

    protected fun url(user: UserModel) = baseUrl + user.slug

    protected fun errorKey(ex: Exception): String {
        if (ex is ConflictException){
            val message = ex.message
            if (
                    message == "duplicate_name" ||
                    message == "duplicate_email" ||
                    message == "syndicate_error" ||
                    message == "publish_error" ||
                    message == "story_already_imported" ||
                    message == "title_missing" ||
                    message == "duplicate_mobile_number"
            ){
                return "error.$message"
            }
        }
        return "error.unexpected"
    }
}
