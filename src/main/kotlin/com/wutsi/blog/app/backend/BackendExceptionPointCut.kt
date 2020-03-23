package com.wutsi.blog.app.backend

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.HttpStatusCodeException


@Aspect
@Configuration
class BackendExceptionPointCut (private val handler: BackendExceptionHandler) {
    @Around("execution(* com.wutsi.blog.app.backend.*.*(..))")
    @Throws(Throwable::class)
    fun translateToDataAccessException2(pjp: ProceedingJoinPoint): Any? {
        try {
            return pjp.proceed()
        } catch (e: HttpStatusCodeException) {
            handler.handleException(e)
            return null
        }
    }
}
