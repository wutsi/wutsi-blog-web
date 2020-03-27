package com.wutsi.blog.app.config

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.wutsi.core.aws.health.S3HealthIndicator
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("aws")
class AwsConfiguration {
    @Value("\${wutsi.storage.s3.region}")
    private lateinit var region: String

    @Value("\${wutsi.storage.s3.bucket}")
    private lateinit var bucket: String

    @Bean
    fun s3Health(): HealthIndicator {
        return S3HealthIndicator(amazonS3(), bucket)
    }

    @Bean
    fun amazonS3(): AmazonS3 {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .build()
    }
}
