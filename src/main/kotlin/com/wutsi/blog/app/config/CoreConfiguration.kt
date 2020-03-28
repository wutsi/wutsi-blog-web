package com.wutsi.blog.app.config

import com.amazonaws.services.s3.AmazonS3
import com.wutsi.blog.app.servlet.StorageServlet
import com.wutsi.core.aws.service.AwsStorageService
import com.wutsi.core.storage.LocalStorageService
import com.wutsi.core.storage.StorageService
import com.wutsi.core.tracking.DeviceUIDFilter
import com.wutsi.core.tracking.DeviceUIDProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
class CoreConfiguration {
    @Value("\${wutsi.storage.local.directory}")
    private lateinit var directory: String

    @Value("\${wutsi.storage.servlet.path}")
    lateinit var servletPath: String

    @Value("\${wutsi.storage.servlet.url}")
    lateinit var servletUrl: String

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
