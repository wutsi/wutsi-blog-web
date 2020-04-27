package com.wutsi.blog.app.config

import com.wutsi.core.cache.CacheService
import com.wutsi.core.cache.HashMapCacheService
import com.wutsi.core.cache.NullCacheService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class CacheConfiguration{

    @Bean
    @Profile("!qa")
    fun cacheService(): CacheService {
        return HashMapCacheService()
    }


    @Bean
    @Profile("qa")
    fun qaCacheService(): CacheService {
        return NullCacheService()
    }
}
