package com.wutsi.blog.app.page.schemas

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.common.service.RequestContext
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class WutsiSchemasGeneratorTest {
    @Test
    fun generate() {
        val requestContext = mock(RequestContext::class.java)
        doReturn("Ze Description").whenever(requestContext).getMessage("wutsi.description", "")

        val generator = WutsiSchemasGenerator(
            ObjectMapper(),
            requestContext,
            "https://www.wutsi.com",
            "https://www.wutsi.com/assets"
        )

        val json = generator.generate()

        val map = ObjectMapper().readValue(json, Map::class.java) as Map<String, Any>
        assertEquals("Organization", map["@type"])
        assertEquals("https://schema.org/", map["@context"])
        assertEquals("Wutsi", map["name"])
        assertEquals("Ze Description", map["description"])
        assertEquals("Ze Description", map["description"])
        assertEquals("https://www.wutsi.com", map["url"])
    }
}
