package com.wutsi.blog.app.common.service

import org.springframework.stereotype.Service

@Service
class SiteProvider {
    /**
     * TODO siteId hardcoded to 1
     */
    fun siteId(): Long = 1L
}
