package com.wutsi.blog.app.page.schemas

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.service.TopicService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class StorySchemasGenerator(
        private val objectMapper: ObjectMapper,
        private val topics: TopicService,
        private val persons: PersonSchemasGenerator,

        @Value("\${wutsi.base-url}") private val baseUrl: String,
        @Value("\${wutsi.asset-url}") private val assetUrl: String
) {

    fun generate(story: StoryModel): String {
        val schemas = mutableMapOf<String, Any>()
        val name = if (story.title.length <= 110) story.title else story.title.substring(0, 110)
        schemas["@context"] = "https://schema.org/"
        schemas["@type"] = "BlogPosting"
        schemas["mainEntityOfPage"] = entity(story)
        schemas["author"] = author(story.user)
        schemas["publisher"] = publisher()
        schemas["url"] = "${baseUrl}${story.slug}"
        schemas["headline"] = name
        schemas["name"] = name
        schemas["identifier"] = story.id.toString()
        schemas["description"] = story.summary
        schemas["dateCreated"] = story.creationDateTimeISO8601
        schemas["dateModified"] = story.modificationDateTimeISO8601
        schemas["keywords"] = keywords(story)
        schemas["mainEntityOfPage"] = "true"
        schemas["isAccessibleForFree"] = "true"
        schemas["inLanguage"] = language(story)
        schemas["hasPart"] = part(story)

        if (story.publishedDateTimeISO8601 != null) {
            schemas["datePublished"] = story.publishedDateTimeISO8601
        }
        if (story.thumbnailUrl != null) {
            schemas["image"] = story.thumbnailUrl
        }

        return objectMapper.writeValueAsString(schemas)
    }

    private fun part(story: StoryModel): Map<String, Any> {
        val schemas = mutableMapOf<String, Any>()
        schemas["@type"] = "WebPageElement"
        schemas["isAccessibleForFree"] = "isAccessibleForFree"
        schemas["cssSelector"] = "article .content"
        return schemas

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

    private fun entity(story: StoryModel): Map<String, Any> {
        val schemas = mutableMapOf<String, Any>()
        schemas["@type"] = "WebPage"
        schemas["@id"] = "${baseUrl}/read/${story.id}"
        return schemas
    }

    private fun author(user: UserModel): Map<String, Any> = persons.generateMap(user)

    private fun publisher(): Map<String, Any> {
        val schemas = mutableMapOf<String, Any>()
        schemas["@type"] = "Organization"
        schemas["name"] = "Wutsi"
        schemas["url"] = "https://www.wutsi.com"
        schemas["logo"] = "${assetUrl}/assets/wutsi/img/logo/logo-512x512.png"
        return schemas
    }

    private fun language(story: StoryModel): String {
        return if (story.language == "fr") "fr-FR" else "en-US"
    }

}
