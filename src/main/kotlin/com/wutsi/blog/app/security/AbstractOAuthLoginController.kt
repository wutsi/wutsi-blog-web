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
    companion object {
        const val CONNECT_KEY = "com.wutsi.connect"
        const val REQUEST_TOKEN_KEY = "com.wutsi.request_token"
    }

    abstract protected fun getAuthorizationUrl (request: HttpServletRequest): String

    abstract protected fun getError(request: HttpServletRequest): String?

    abstract protected fun getSigninUrl(request: HttpServletRequest): String

    open protected fun getConnectUrl(request: HttpServletRequest): String {
       return getSigninUrl(request)
    }

    open protected fun cleanup (request: HttpServletRequest) {
        request.session.removeAttribute(CONNECT_KEY)
        request.session.removeAttribute(REQUEST_TOKEN_KEY)
    }

    @GetMapping()
    fun login (request: HttpServletRequest): String {
        val connect = request.getParameter("connect")
        if (connect != null) {
            request.session.setAttribute(CONNECT_KEY, connect)
        }

        val url = getAuthorizationUrl(request)
        return "redirect:$url"
    }

    @GetMapping("/callback")
    open fun callback(request: HttpServletRequest): String {
        var url: String
        try {

            var url: String = ""
            val error = getError(request)
            val connect = request.session.getAttribute(CONNECT_KEY)
            if (connect == null) {
                url = if (error == null) getSigninUrl(request) else "/login?error=" + URLEncoder.encode(error, "utf-8")
            } else {
                url = if (error == null) getConnectUrl(request) else "/channel?error=" + URLEncoder.encode(error, "utf-8")
            }

            logger.add("URL", url)
            return "redirect:$url"

        } catch(ex: OAuthException) {

            url = errorUrl(ex.message!!)
            logger.add("Exception", ex.javaClass.name)
            logger.add("ExceptionMessage", ex.message)
            LoggerFactory.getLogger(javaClass).error("Failure", ex)

        } finally {

            cleanup(request)

        }

        logger.add("RedirectURL", url)
        return "redirect:$url"
    }

    private fun errorUrl(error: String) : String {
        return "/login?error=" + URLEncoder.encode(error, "utf-8")
    }
}
