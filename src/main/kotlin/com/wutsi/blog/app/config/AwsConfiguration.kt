package com.wutsi.blog.app.config

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("aws")
class AwsConfiguration {
    @Value("\${wutsi.aws.s3.region}")
    private lateinit var region: String


    @Bean
    fun amazonS3(): AmazonS3 {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .build()
    }
}
