package com.wutsi.blog.app.config

import com.wutsi.core.cache.CacheService
import com.wutsi.core.cache.HashMapCacheService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfiguration{

    @Bean
    fun cacheService(): CacheService {
        return HashMapCacheService()
    }

}
