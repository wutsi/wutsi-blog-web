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
class InstantMessagingNextActionTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @InjectMocks
    private lateinit var nba: InstantMessagingNextAction

    @Test
    fun getInstantMessaging() {
        assertNull(nba.get(UserModel(hasInstantMessagingLinks = true), emptyList()))
    }

    @Test
    fun getNoInstantMessaging() {
        testNoInstantMessaging("")
        testNoInstantMessaging(null)
    }

    private fun testNoInstantMessaging(value: String?) {
        doReturn("This is title").whenever(requestContext).getMessage(any(), eq(null), eq(null), eq(null))

        val result = nba.get(
            UserModel(
                whatsappId = value,
                telegramId = value
            ),
            emptyList()
        )

        assertEquals("instant_messaging", result?.name)
        assertEquals("/assets/wutsi/img/social/whatsapp.png", result?.iconUrl)
        assertEquals("/me/settings?highlight=instant_messaging-container#instant_messaging", result?.url)
        assertEquals("This is title", result?.title)
    }
}
