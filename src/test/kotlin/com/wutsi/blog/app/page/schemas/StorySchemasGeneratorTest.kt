package com.wutsi.blog.app.page.schemas

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.model.TagModel
import com.wutsi.blog.app.page.story.model.TopicModel
import com.wutsi.blog.app.page.story.service.TopicService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StorySchemasGeneratorTest {
    @Mock
    lateinit var topics: TopicService

    @Test
    fun generate() {
        val parent = createTopic(1L, 0L, "Topic1")
        val topic = createTopic(11L, 1L, "Topic11")
        `when`(topics.get(1L)).thenReturn(parent)

        val requestContext = mock(RequestContext::class.java)

        val baseUrl = "https://www.wutsi.com"
        val om = ObjectMapper()
        val generator = StorySchemasGenerator(
                ObjectMapper(),
                topics,
                PersonSchemasGenerator(om, baseUrl),
                WutsiSchemasGenerator(om, requestContext, baseUrl, "https://www.wutsi.com/assets"),
                baseUrl
        )

        var story = StoryModel(
                id = 123L,
                title = "This is the title",
                summary = "This is summary",
                slug = "/read/11/troirto",
                topic = topic,
                language = "fr",
                thumbnailUrl = "https://www.wutsi.com/assets/foo/1.png",
                user = createUser(1, "Ray Sponsible", "/@/ray.sponsible"),
                modificationDateTimeISO8601 = "1994-11-05T08:15:30-05:00",
                publishedDateTimeISO8601 = "1994-11-05T08:15:30-05:00",
                creationDateTimeISO8601 = "1994-11-05T08:15:30-05:00",
                tags = arrayListOf(
                        TagModel(id = 1, displayName = "Art"),
                        TagModel(id = 2, displayName = "Comics")
                ),
                wordCount = 1243
        )
        val json = generator.generate(story)

        val map = ObjectMapper().readValue(json, Map::class.java) as Map<String, Any>
        assertEquals("BlogPosting", map["@type"])
        assertEquals("https://schema.org/", map["@context"])
        assertEquals(story.id.toString(), map["identifier"])
        assertEquals(story.title, map["name"])
        assertEquals(story.title, map["headline"])
        assertEquals(story.creationDateTimeISO8601, map["dateCreated"])
        assertEquals(story.modificationDateTimeISO8601, map["dateModified"])
        assertEquals(story.publishedDateTimeISO8601, map["datePublished"])
        assertEquals("https://www.wutsi.com/read/11/troirto", map["url"])
        assertEquals(story.thumbnailUrl, map["image"])
        assertEquals(story.summary, map["description"])
        assertEquals("true", map["isAccessibleForFree"])
        assertEquals("https://www.wutsi.com/read/11/troirto", map["mainEntityOfPage"])
        assertEquals("fr-FR", map["inLanguage"])

    }

    private fun createTopic(id: Long, parentId: Long, name: String) = TopicModel(
            id = id,
            parentId = parentId,
            displayName = name
    )

    private fun createUser(id: Long, fullName: String, slug: String) = UserModel(
            id = id,
            fullName = fullName,
            slug = slug
    )
}
