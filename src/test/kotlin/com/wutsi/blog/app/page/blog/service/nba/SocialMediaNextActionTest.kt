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
class SocialMediaNextActionTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @InjectMocks
    private lateinit var nba: SocialMediaNextAction

    @Test
    fun `return null when social media ID is set`() {
        assertNull(nba.get(UserModel(hasSocialLinks = true), emptyList()))
    }

    @Test
    fun `return action when has no social media links`() {
        testAction()
    }

    private fun testAction() {
        doReturn("This is title").whenever(requestContext).getMessage(any(), eq(null), eq(null), eq(null))

        val result = nba.get(
            UserModel(
                hasSocialLinks = false
            ),
            emptyList()
        )

        assertEquals("social_media", result?.name)
        assertEquals("/assets/wutsi/img/social_media.png", result?.iconUrl)
        assertEquals("/me/settings?highlight=social_media-container#social_media", result?.url)
        assertEquals("This is title", result?.title)
    }
}
