package com.wutsi.blog.app.controller

import com.wutsi.blog.app.model.PageModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
class WutsiErrorController(
        requestContext: RequestContext
): ErrorController, AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WutsiErrorController::class.java)
    }

    override fun pageName(): String = ""

    @GetMapping("/error")
    fun error(request: HttpServletRequest, model: Model): String {
        val code: Int? = request.getAttribute("javax.servlet.error.status_code") as Int
        val exception = request.getAttribute("javax.servlet.error.exception")
        val message = request.getAttribute("javax.servlet.error.message") as String
        if (exception != null) {
            LOGGER.error("StatusCode=$code - $message", exception as Throwable)
        } else {
            LOGGER.error("StatusCode=$code - $message")
        }

        model.addAttribute(ModelAttributeName.PAGE, toPage(code))
        if (code == 400){
            return "page/error/404"
        } else if (code == 403){
            return "page/error/404"
        } else if (code == 404){
            return "page/error/404"
        } else {
            return "page/error/500"
        }
    }

    override fun getErrorPath(): String {
        return "/error"
    }

    private fun toPage(code: Int?) = PageModel(
            name = pageName(code),
            title = requestContext.getMessage("wutsi.title"),
            description = requestContext.getMessage("wutsi.description"),
            type = "website",
            assetUrl = assetUrl,
            baseUrl = baseUrl,
            robots = robots()
    )

    private fun pageName(code: Int?): String {
        if (code == 400){
            return PageName.ERROR_400
        } else if (code == 403){
            return PageName.ERROR_403
        } else if (code == 404){
            return PageName.ERROR_404
        } else {
            return PageName.ERROR_500
        }
    }
}
