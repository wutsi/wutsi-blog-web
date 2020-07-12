package com.wutsi.blog.app.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.exceptions.OAuthException
import com.wutsi.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import java.net.URLEncoder
import javax.servlet.http.HttpServletRequest


abstract class AbstractOAuthLoginController(
        logger: KVLogger,
        objectMapper: ObjectMapper
) : AbstractLoginController(logger, objectMapper) {

    abstract protected fun getAuthorizationUrl (request: HttpServletRequest): String

    abstract protected fun getError(request: HttpServletRequest): String?

    abstract protected fun getSigninUrl(request: HttpServletRequest): String

    @GetMapping()
    fun login (request: HttpServletRequest): String {
        val url = getAuthorizationUrl(request)
        return "redirect:$url"
    }

    @GetMapping("/callback")
    open fun callback(request: HttpServletRequest): String {
        var url: String
        try {

            val error = getError(request)
            url = if (error == null) getSigninUrl(request) else errorUrl(error)

        } catch(ex: OAuthException) {

            url = errorUrl(ex.message!!)
            logger.add("Exception", ex.javaClass.name)
            logger.add("ExceptionMessage", ex.message)
            LoggerFactory.getLogger(javaClass).error("Failure", ex)
        }

        logger.add("RedirectURL", url)
        return "redirect:$url"
    }

    private fun errorUrl(error: String) : String {
        return "/login?error=" + URLEncoder.encode(error, "utf-8")
    }
}
