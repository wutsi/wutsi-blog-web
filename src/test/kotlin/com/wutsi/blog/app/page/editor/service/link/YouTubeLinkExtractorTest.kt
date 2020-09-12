package com.wutsi.blog.app.page.editor.service.link

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
class YouTubeLinkExtractorTest {
    @Autowired
    private lateinit var extractor: YouTubeLinkExtractor

    @Test
    fun acceptYouTubeVideo (){
        val url = "https://www.youtube.com/watch?v=buS6MIrPBuc"
        assertTrue(extractor.accept(url))
    }

    @Test
    fun acceptInvalid (){
        val url = "https://www.google.ca"
        assertFalse(extractor.accept(url))
    }

    @Test
    fun test (){
        val url = "https://www.youtube.com/watch?v=buS6MIrPBuc"
        val meta = extractor.extract(url)
        assertEquals("Old School | Funk Mix 80s (113bpm) [Dj'S Bootleg Dance Re-Mix]", meta.title)
        assertEquals("", meta.description)
        assertEquals("YouTube", meta.site_name)
        assertEquals("https://i.ytimg.com/vi/buS6MIrPBuc/sddefault.jpg", meta.image.url)
    }
}
