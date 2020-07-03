package com.wutsi.blog.app.controller

import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.core.exception.ConflictException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
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

    @Value("\${wutsi.oauth.google.client-id}")
    protected lateinit var googleClientId: String

    protected abstract fun pageName(): String

    @ModelAttribute(ModelAttributeName.USER)
    fun getUser() = requestContext.currentUser()

    @ModelAttribute(ModelAttributeName.SUPER_USER)
    fun getSuperUser() = requestContext.currentSuperUser()

    @ModelAttribute(ModelAttributeName.TOGGLES)
    fun getToggles() = requestContext.toggles()

    @ModelAttribute(ModelAttributeName.PAGE)
    fun getPage() = page()

    @ModelAttribute(ModelAttributeName.HITID)
    fun getHitId() = UUID.randomUUID().toString()

    open fun shouldBeIndexedByBots() = false

    open fun shouldShowGoogleOneTap() = false

    protected fun getPageRobotsHeader () = if (shouldBeIndexedByBots()) "index,follow" else "noindex,nofollow"

    open fun page() = createPage(
            title = requestContext.getMessage("wutsi.title"),
            description = requestContext.getMessage("wutsi.description")
    )

    protected fun createPage(title: String, description: String, type: String = "website") = PageModel(
            name = pageName(),
            title = title,
            description = description,
            type = type,
            robots = getPageRobotsHeader(),
            baseUrl = baseUrl,
            assetUrl = assetUrl,
            googleAnalyticsCode = this.googleAnalyticsCode,
            facebookPixelCode = this.facebookPixelId,
            googleClientId = this.googleClientId,
            showGoogleOneTap = shouldShowGoogleOneTap(),
            language = LocaleContextHolder.getLocale().language
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
                    message == "duplicate_mobile_number" ||
                    message == "permission_denied"
            ){
                return "error.$message"
            }
        }
        return "error.unexpected"
    }
}
