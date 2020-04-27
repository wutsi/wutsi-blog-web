package com.wutsi.blog.app.backend

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.client.story.GetStoryResponse
import com.wutsi.blog.client.story.ImportStoryRequest
import com.wutsi.blog.client.story.ImportStoryResponse
import com.wutsi.blog.client.story.PublishStoryRequest
import com.wutsi.blog.client.story.PublishStoryResponse
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SearchStoryResponse
import com.wutsi.blog.client.story.StoryDto
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.core.cache.CacheService
import com.wutsi.core.http.Http
import com.wutsi.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StoryBackend (
        private val http: Http,
        private val cache: CacheService,
        private val logger: KVLogger,
        private val objectMapper: ObjectMapper,
        @Value("\${wutsi.cache.story-ttl-seconds}") private val storyCacheTTL: Int
) {
    @Value("\${wutsi.backend.story.endpoint}")
    private lateinit var endpoint: String

    fun create(request: SaveStoryRequest): SaveStoryResponse {
        return http.post(endpoint, request, SaveStoryResponse::class.java).body!!
    }

    fun update(id: Long, request: SaveStoryRequest): SaveStoryResponse {
        val response = http.post("$endpoint/$id", request, SaveStoryResponse::class.java).body!!
        deleteFromCache(id)
        return response
    }

    fun get(id:Long): GetStoryResponse {
        var response = getFromCache(id)
        if (response != null) {
            return response
        }

        response = http.get("$endpoint/$id", GetStoryResponse::class.java).body!!
        putToCache(response.story)
        return response
    }

    fun readability(id:Long): GetStoryReadabilityResponse {
        return http.get("$endpoint/$id/readability", GetStoryReadabilityResponse::class.java).body!!
    }

    fun search(request: SearchStoryRequest): SearchStoryResponse {
        return http.post("$endpoint/search", request, SearchStoryResponse::class.java).body!!
    }

    fun count(request: SearchStoryRequest): CountStoryResponse {
        return http.post("$endpoint/count", request, CountStoryResponse::class.java).body!!
    }

    fun publish(id:Long, request: PublishStoryRequest): PublishStoryResponse {
        val response = http.post("$endpoint/$id/publish", request, PublishStoryResponse::class.java).body!!
        deleteFromCache(id)
        return response
    }

    fun import(request: ImportStoryRequest): ImportStoryResponse {
        return http.post("$endpoint/import", request, ImportStoryResponse::class.java).body!!
    }

    fun getFromCache(id: Long): GetStoryResponse? {
        var exception: Exception? = null
        val key = cacheKey(id)
        var success = false

        try {
            val json = cache.get(key)
            if (json != null){
                val story = objectMapper.readValue(json, StoryDto::class.java)
                success = true
                return GetStoryResponse(story = story)
            }

        } catch (ex: Exception) {
            exception = ex
        } finally {
            logCache("GET", key, success, exception)
        }
        return null
    }

    fun putToCache(story: StoryDto) {
        if (story.status != StoryStatus.published) {
            return
        }

        var exception: Exception? = null
        val key = cacheKey(story.id)

        try {
            val value = objectMapper.writeValueAsString(story)
            cache.put(key, value, storyCacheTTL)
        } catch (ex: Exception) {
            exception = ex
        } finally {
            logCache("PUT", key, true, exception)
        }
    }

    fun deleteFromCache(id: Long) {
        var exception: Exception? = null
        val key = cacheKey(id)

        try {
            cache.remove(key)
        } catch (ex: Exception) {
            exception = ex
        } finally {
            logCache("DELETE", key, true, exception)
        }
    }

    private fun cacheKey(id: Long) = "story.$id"

    private fun logCache(operation: String, key: String, success: Boolean, ex: Exception? = null){
        logger.add("Cache${operation}Key", key)
        logger.add("Cache${operation}Success", success && ex == null)
        if (ex != null) {
            logger.add("Cache${operation}Exception", ex.javaClass.name)
            logger.add("Cache${operation}ExceptionMessage", ex.message)
        }
    }
}
