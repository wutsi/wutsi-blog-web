package com.wutsi.blog.app.page.blog.service.nba

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.model.UserModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class BiographyNextActionTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @InjectMocks
    private lateinit var nba: BiographyNextAction

    @Test
    fun get() {
        val blog = UserModel(biography = "yo")

        val result = nba.get(blog, emptyList())
        assertNull(result)
    }

    @Test
    fun getNullDescription() {
        doReturn("This is title").whenever(requestContext).getMessage(any(), eq(null), eq(null), eq(null))

        val blog = UserModel(biography = null)

        val result = nba.get(blog, emptyList())
        assertEquals("biography", result?.name)
        assertEquals("/assets/wutsi/img/settings.png", result?.iconUrl)
        assertEquals("/me/settings?highlight=biography-container#general", result?.url)
        assertEquals("This is title", result?.title)
    }

    @Test
    fun getEmptyDescription() {
        doReturn("This is title").whenever(requestContext).getMessage(any(), eq(null), eq(null), eq(null))

        val blog = UserModel(biography = null)

        val result = nba.get(blog, emptyList())
        assertEquals("biography", result?.name)
        assertEquals("/assets/wutsi/img/settings.png", result?.iconUrl)
        assertEquals("/me/settings?highlight=biography-container#general", result?.url)
        assertEquals("This is title", result?.title)
    }
}
