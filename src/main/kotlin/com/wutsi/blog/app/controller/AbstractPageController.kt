package com.wutsi.blog.app.controller

import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.ModelAttributeName
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ModelAttribute

abstract class AbstractPageController(
        protected val requestContext: RequestContext
) {
    @Value("\${wutsi.base-url}")
    protected lateinit var baseUrl: String

    protected abstract fun pageName(): String


    @ModelAttribute(ModelAttributeName.USER)
    fun getUser() = requestContext.currentUser()

    @ModelAttribute(ModelAttributeName.TOGGLES)
    fun getToggles() = requestContext.toggles()

    @ModelAttribute(ModelAttributeName.PAGE)
    fun getPage() = page()

    open protected fun shouldBeIndexedByBots() = false

    protected fun robots () = if (shouldBeIndexedByBots()) "all" else "noindex,nofollow"

    protected fun page() = PageModel(
            name = pageName(),
            title = requestContext.getMessage("wutsi.title"),
            description = requestContext.getMessage("wutsi.description"),
            type = "website",
            robots = robots(),
            baseUrl = baseUrl
    )
}
