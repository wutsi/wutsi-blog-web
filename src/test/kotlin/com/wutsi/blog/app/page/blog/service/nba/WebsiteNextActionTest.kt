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
class WebsiteNextActionTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @InjectMocks
    private lateinit var nba: WebsiteNextAction

    @Test
    fun `return null when website url is set`() {
        assertNull(nba.get(UserModel(websiteUrl = "https://www.google.com"), emptyList()))
    }

    @Test
    fun `return action when no website url is empty`() {
        testAction("")
    }

    @Test
    fun `return action when no website url is null`() {
        testAction(null)
    }

    private fun testAction(websiteUrl: String?) {
        doReturn("This is title").whenever(requestContext).getMessage(any(), eq(null), eq(null), eq(null))

        val result = nba.get(UserModel(websiteUrl = websiteUrl), emptyList())

        assertEquals("website", result?.name)
        assertEquals("/assets/wutsi/img/website.png", result?.iconUrl)
        assertEquals("/me/settings?highlight=website-container#website", result?.url)
        assertEquals("This is title", result?.title)
    }
}
