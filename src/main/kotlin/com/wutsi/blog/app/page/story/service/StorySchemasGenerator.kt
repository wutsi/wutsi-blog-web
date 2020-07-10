package com.wutsi.blog.app.page.story.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.story.model.StoryModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StorySchemasGenerator(
        private val objectMapper: ObjectMapper,
        private val topics: TopicService,

        @Value("\${wutsi.base-url}") private val baseUrl: String,
        @Value("\${wutsi.asset-url}") private val assetUrl: String
) {

    fun generate(story: StoryModel): String {
        val schemas = mutableMapOf<String, Any>()

        schemas["@context"] = "https://schema.org/"
        schemas["@type"] = "BlogPosting"
        schemas["mainEntityOfPage"] = entitiy(story)
        schemas["author"] = author(story.user)
        schemas["publisher"] = publisher()
        schemas["url"] = "${baseUrl}${story.slug}"
        schemas["headline"] = if (story.title.length <= 110) story.title else story.title.substring(0, 110)
        schemas["description"] = story.summary
        schemas["dateCreated"] = story.creationDateTimeISO8601
        schemas["dateModified"] = story.modificationDateTimeISO8601
        schemas["keywords"] = keywords(story)
        schemas["mainEntityOfPage"] = "true"

        if (story.publishedDateTimeISO8601 != null) {
            schemas["datePublished"] = story.publishedDateTimeISO8601
        }
        if (story.thumbnailUrl != null) {
            schemas["image"] = story.thumbnailUrl
        }

        return objectMapper.writeValueAsString(schemas)
    }

    private fun keywords(story: StoryModel): List<String> {
        val keywords = mutableListOf<String>()
        keywords.add(story.topic.displayName)

        val parentTopic = topics.get(story.topic.parentId)
        if (parentTopic != null) {
            keywords.add(parentTopic?.displayName)
        }

        keywords.addAll(story.tags.map { it.displayName })

        return keywords
    }

    private fun entitiy(story: StoryModel): Map<String, Any> {
        val schemas = mutableMapOf<String, Any>()
        schemas["@type"] = "WebPage"
        schemas["@id"] = "${baseUrl}/read/${story.id}"
        return schemas
    }

    private fun author(user: UserModel): Map<String, Any> {
        val schemas = mutableMapOf<String, Any>()
        schemas["@type"] = "Person"
        schemas["name"] = user.fullName
        schemas["url"] = "${baseUrl}${user.slug}"
        return schemas
    }

    private fun publisher(): Map<String, Any> {
        val schemas = mutableMapOf<String, Any>()
        schemas["@type"] = "Organization"
        schemas["name"] = "Wutsi"
        schemas["url"] = "https://www.wutsi.com"
        schemas["logo"] = logo()
        return schemas
    }

    private fun logo(): Map<String, String> {
        val schemas = mutableMapOf<String, String>()
        schemas["@type"] = "ImageObject"
        schemas["name"] = "Wutsi"
        schemas["url"] = "${assetUrl}//assets/wutsi/img/logo/logo-512x512.png"
        schemas["width"] = "512"
        schemas["width"] = "512"
        return schemas
    }
}
