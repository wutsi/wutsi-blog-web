package com.wutsi.blog.app.page.editor.service.link

import com.wutsi.blog.SeleniumTestSupport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
class DefaultLinkExtractorTest : SeleniumTestSupport() {
    @Autowired
    private lateinit var extractor: DefaultLinkExtractor

    @Test
    fun acceptYouTubeVideo() {
        val url = "https://www.youtube.com/watch?v=buS6MIrPBuc"
        assertTrue(extractor.accept(url))
    }

    @Test
    fun acceptAny() {
        val url = "https://www.google.ca"
        assertTrue(extractor.accept(url))
    }

    @Test
    fun test() {
        stub(HttpMethod.GET, "/kamerkongossa.html", HttpStatus.OK, "v1/static/kamerkongossa.html", contentType = "text/html")

        val url = "http://localhost:8080/kamerkongossa.html"
        val meta = extractor.extract(url)
        assertEquals("Yaoundé: on rencontre le sous-développement par les chemins qu’on emprunte pour l’éviter - Kamer Kongossa", meta.title)
        assertEquals("Mon bonjour glisse sur les trois premières sans faire de bruit ni obtenir de réponses, . La quatrième, maugrée un charabia inaudible et se lève en tchuipant", meta.description)
        assertEquals("Kamer Kongossa", meta.site_name)
        assertEquals("https://kamerkongossa.cm/wp-content/uploads/2020/01/bain-de-boue.jpg", meta.image.url)
    }
}
