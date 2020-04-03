package com.wutsi.blog.app.config

import com.amazonaws.services.s3.AmazonS3
import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.servlet.StorageServlet
import com.wutsi.core.aws.service.AwsStorageService
import com.wutsi.core.http.Http
import com.wutsi.core.http.HttpExceptionHandler
import com.wutsi.core.http.RequestTraceContext
import com.wutsi.core.http.TraceContextProvider
import com.wutsi.core.storage.LocalStorageService
import com.wutsi.core.storage.StorageService
import com.wutsi.core.tracking.DeviceUIDFilter
import com.wutsi.core.tracking.DeviceUIDProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest


@Configuration
class CoreConfiguration(
        private val objectMapper: ObjectMapper,
        private val context: ApplicationContext
) {
    @Value("\${wutsi.storage.local.directory}")
    private lateinit var directory: String

    @Value("\${wutsi.storage.servlet.path}")
    lateinit var servletPath: String

    @Value("\${wutsi.storage.servlet.url}")
    lateinit var servletUrl: String

    @Value("\${wutsi.http.client-id}")
    lateinit var clientId: String


    @Bean
    fun http() : Http {
        return Http(
                rest = restTemplate(),
                exceptionHandler = httpExceptionHandler(),
                traceContextProvider = traceContextProvider()
        )
    }

    @Bean
    fun restTemplate() = RestTemplate()

    @Bean
    fun httpExceptionHandler() = HttpExceptionHandler(objectMapper)

    @Bean
    fun traceContextProvider() = TraceContextProvider(clientId, context)

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun requestTraceContext(request: HttpServletRequest) = RequestTraceContext(request)

    @Bean
    fun storageServlet(): ServletRegistrationBean<*> {
        val bean = ServletRegistrationBean(StorageServlet(directory), "${servletPath}/*")
        bean.setLoadOnStartup(1)
        return bean
    }

    @Bean
    @Profile("!aws")
    fun localStorageService(): StorageService {
        return LocalStorageService(directory, servletUrl)
    }

    @Bean
    @Profile("aws")
    @Autowired
    fun awsStorageService(s3: AmazonS3) : StorageService {
        return AwsStorageService(s3)
    }

    @Bean
    fun deviceUIDFilter () : DeviceUIDFilter {
        return DeviceUIDFilter(DeviceUIDProvider())
    }
}
