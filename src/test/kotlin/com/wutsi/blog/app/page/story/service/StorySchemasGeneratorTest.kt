package com.wutsi.blog.app.page.story.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.app.page.story.model.StoryModel
import com.wutsi.blog.app.page.story.model.TagModel
import com.wutsi.blog.app.page.story.model.TopicModel
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
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

        val generator = StorySchemasGenerator(
                ObjectMapper(),
                topics,
                "https://www.wutsi.com",
                "https://www.wutsi.com/assets"
        )

        var story = StoryModel(
                id = 123L,
                title = "This is the title",
                summary = "This is summary",
                slug = "/read/11/troirto",
                topic = topic,
                language = "fr",
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

        val expected = "{" +
                "\"@context\":\"https://schema.org/\"," +
                "\"@type\":\"BlogPosting\"," +
                "\"mainEntityOfPage\":\"true\"," +
                "\"author\":{" +
                "\"@type\":\"Person\"," +
                "\"name\":\"Ray Sponsible\"," +
                "\"url\":\"https://www.wutsi.com/@/ray.sponsible\"" +
                "}," +
                "\"publisher\":{" +
                "\"@type\":\"Organization\"," +
                "\"name\":\"Wutsi\"," +
                "\"url\":\"https://www.wutsi.com\"," +
                "\"logo\":{" +
                "\"@type\":\"ImageObject\"," +
                "\"name\":\"Wutsi\"," +
                "\"url\":\"https://www.wutsi.com/assets//assets/wutsi/img/logo/logo-512x512.png\"," +
                "\"width\":\"512\"" +
                "}" +
                "}," +
                "\"url\":\"https://www.wutsi.com/read/11/troirto\"," +
                "\"headline\":\"This is the title\"," +
                "\"description\":\"This is summary\"," +
                "\"dateCreated\":\"1994-11-05T08:15:30-05:00\"," +
                "\"dateModified\":\"1994-11-05T08:15:30-05:00\"," +
                "\"keywords\":[\"Topic11\",\"Topic1\",\"Art\",\"Comics\"]," +
                "\"datePublished\":\"1994-11-05T08:15:30-05:00\"" +
                "}"
        Assert.assertEquals(expected, json)
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
