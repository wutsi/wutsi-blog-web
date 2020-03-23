package com.wutsi.blog.app.backend

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.client.error.ErrorResponse
import com.wutsi.core.exception.BadRequestException
import com.wutsi.core.exception.ConflictException
import com.wutsi.core.exception.ForbiddenException
import com.wutsi.core.exception.NotFoundException
import com.wutsi.core.exception.UnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpStatusCodeException

@Service
class BackendExceptionHandler {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(BackendExceptionHandler::class.java)
    }

    fun handleException(ex: Exception) {
        if (ex is HttpStatusCodeException){
            handleException(ex)
        } else {
            throw ex
        }
    }

    private fun handleException(e: HttpStatusCodeException) {
        val body = e.responseBodyAsString
        LOGGER.error("Backend error. HttpStatus=${e.statusCode} - ResponseBody=$body", e)

        val response = extractResponse(body)
        val message = if (response.error.code.isEmpty()) body else response.error.code

        if (e.statusCode == HttpStatus.NOT_FOUND){
            throw NotFoundException(message, e)
        } else if (e.statusCode == HttpStatus.CONFLICT){
            throw ConflictException(message, e)
        } else if (e.statusCode == HttpStatus.UNAUTHORIZED){
            throw UnauthorizedException(message, e)
        } else if (e.statusCode == HttpStatus.BAD_REQUEST){
            throw BadRequestException(message, e)
        } else if (e.statusCode == HttpStatus.FORBIDDEN){
            throw ForbiddenException(message, e)
        } else {
            throw e
        }
    }

    private fun extractResponse(body: String ): ErrorResponse {
        try {
            return ObjectMapper().readValue(body, ErrorResponse::class.java)
        } catch (e: Exception){
            return ErrorResponse()
        }
    }
}
