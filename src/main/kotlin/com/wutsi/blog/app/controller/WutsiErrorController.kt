package com.wutsi.blog.app.controller

import com.wutsi.blog.app.util.ModelAttributeName
import com.wutsi.blog.app.util.PageName
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
class WutsiErrorController: ErrorController {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WutsiErrorController::class.java)
    }

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

        /* error code */
        if (code == 400){
            model.addAttribute(ModelAttributeName.PAGE_NAME, PageName.ERROR_400)
            return "page/error/404"
        } else if (code == 403){
            model.addAttribute(ModelAttributeName.PAGE_NAME, PageName.ERROR_403)
            return "page/error/404"
        } else if (code == 404){
            model.addAttribute(ModelAttributeName.PAGE_NAME, PageName.ERROR_404)
            return "page/error/404"
        } else {
            model.addAttribute(ModelAttributeName.PAGE_NAME, PageName.ERROR_500)
            return "page/error/500"
        }
    }

    override fun getErrorPath(): String {
        return "/error"
    }
}
