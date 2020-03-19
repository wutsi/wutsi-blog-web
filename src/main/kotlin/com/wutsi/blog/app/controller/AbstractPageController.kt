package com.wutsi.blog.app.controller

import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.ModelAttributeName
import org.springframework.web.bind.annotation.ModelAttribute

abstract class AbstractPageController(
        protected val requestContext: RequestContext
) {
    @ModelAttribute(ModelAttributeName.PAGE_NAME)
    fun getPageName() = page()

    @ModelAttribute(ModelAttributeName.USER)
    fun getUser() = requestContext.currentUser()

    @ModelAttribute(ModelAttributeName.TOGGLES)
    fun getToggles() = requestContext.toggles()

    protected abstract fun page(): String
}
