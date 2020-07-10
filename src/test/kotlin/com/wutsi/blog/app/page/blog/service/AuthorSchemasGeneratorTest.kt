package com.wutsi.blog.app.page.blog.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.page.settings.model.UserModel
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthorSchemasGeneratorTest {
    private val generator = AuthorSchemasGenerator(
            ObjectMapper(),
            "https://www.wutsi.com"
    )

    @Test
    fun generate() {
        val author = UserModel(
                id = 1L,
                name = "ray.sponsible",
                fullName = "Ray Sponsible",
                slug = "/@/ray.sponsible",
                pictureUrl = "https://www.picture.com/ray.sponsible.png",
                hasSocialLinks = true,
                facebookUrl = "https://www.facebook.com/ray.sponsible",
                twitterUrl = "https://www.twitter.com/ray.sponsible",
                youtubeUrl = "https://www.youtube.com/user/ray.sponsible",
                linkedinUrl = "https://www.linkedin.com/user/ray.sponsible"
        )
        val json = generator.generate(author)

        val expected = "{" +
                "\"@context\":\"https://schema.org/\"," +
                "\"@type\":\"Person\",\"name\":\"Ray Sponsible\"," +
                "\"url\":\"https://www.wutsi.com/@/ray.sponsible\"," +
                "\"image\":\"https://www.picture.com/ray.sponsible.png\"," +
                "\"sameAs\":[" +
                    "\"https://www.facebook.com/ray.sponsible\"," +
                    "\"https://www.linkedin.com/user/ray.sponsible\"," +
                    "\"https://www.youtube.com/user/ray.sponsible\"," +
                    "\"https://www.twitter.com/ray.sponsible\"" +
                "]}"
        assertEquals(expected, json)
    }

}
