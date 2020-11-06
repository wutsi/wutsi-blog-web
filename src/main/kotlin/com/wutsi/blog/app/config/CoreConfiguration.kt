package com.wutsi.blog.app.config

import com.amazonaws.services.s3.AmazonS3
import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.core.aws.health.S3HealthIndicator
import com.wutsi.core.aws.service.S3StorageService
import com.wutsi.core.http.Http
import com.wutsi.core.http.HttpExceptionHandler
import com.wutsi.core.http.RequestTraceContext
import com.wutsi.core.http.TraceContextProvider
import com.wutsi.core.logging.KVLogger
import com.wutsi.core.logging.KVLoggerFilter
import com.wutsi.core.logging.KVLoggerImpl
import com.wutsi.core.storage.LocalStorageService
import com.wutsi.core.storage.StorageService
import com.wutsi.core.storage.StorageServlet
import com.wutsi.core.tracking.DeviceUIDFilter
import com.wutsi.core.tracking.DeviceUIDProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.WebApplicationContext
import java.time.Clock
import javax.servlet.http.HttpServletRequest


@Configuration
class CoreConfiguration(
        private val objectMapper: ObjectMapper,
        private val clock: Clock,
        private val context: ApplicationContext
) {
    @Bean
    fun http(
            @Value("\${wutsi.http.client-id}") clientId: String
    ) : Http {
        return Http(
                rest = restTemplate(),
                exceptionHandler = HttpExceptionHandler(objectMapper),
                traceContextProvider = TraceContextProvider(clientId, context)
        )
    }

    @Bean
    fun restTemplate() = RestTemplate()

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun requestTraceContext(request: HttpServletRequest) = RequestTraceContext(request)

    @Bean
    @Profile("!aws")
    fun localStorageServlet(
            @Value("\${wutsi.storage.local.directory}") directory: String,
            @Value("\${wutsi.storage.servlet.path}") servletPath: String
    ): ServletRegistrationBean<*> {
        val bean = ServletRegistrationBean(StorageServlet(logger(), directory), "$servletPath/*")
        bean.setLoadOnStartup(1)
        return bean
    }

    @Bean
    @Profile("!aws")
    fun localStorageService(
            @Value("\${wutsi.storage.local.directory}") directory: String,
            @Value("\${wutsi.storage.servlet.url}") servletUrl: String
    ): StorageService {
        return LocalStorageService(directory, servletUrl)
    }

    @Bean
    @Profile("aws")
    @Autowired
    fun s3StorageService(
            s3: AmazonS3,
            @Value("\${wutsi.storage.s3.bucket}") bucket: String
    ) : StorageService {
        return S3StorageService(s3, bucket)
    }

    @Bean
    @Profile("aws")
    @Autowired
    fun s3StorageHealth(
            s3: AmazonS3,
            @Value("\${wutsi.storage.s3.bucket}") bucket: String
    ): HealthIndicator {
        return S3HealthIndicator(s3, bucket)
    }


    @Bean
    fun deviceUIDFilter () : DeviceUIDFilter {
        return DeviceUIDFilter(deviceUIDProvider())
    }

    @Bean
    fun deviceUIDProvider(): DeviceUIDProvider {
        return DeviceUIDProvider()
    }

    @Bean
    fun loggerFilter(): KVLoggerFilter {
        return KVLoggerFilter(logger(), clock)
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    fun logger(): KVLogger = KVLoggerImpl()
}
