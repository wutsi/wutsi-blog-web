package com.wutsi.blog.app.page.blog.service.nba

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.settings.model.UserModel
import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LinkedInNextActionTest {
    @Mock
    private lateinit var requestContext: RequestContext

    @InjectMocks
    private lateinit var nba: LinkedInNextAction

    @Test
    fun getLinkedIn() {
        assertNull(nba.get(UserModel(linkedinId = "1232")))
    }

    @Test
    fun getNoLinkedIn() {
        testNotLinkedIn("")
        testNotLinkedIn(null)
    }

    private fun testNotLinkedIn(linkedinId: String?) {
        doReturn("This is title").whenever(requestContext).getMessage(any(), eq(null), eq(null), eq(null))

        val result = nba.get(UserModel(linkedinId = linkedinId))

        Assert.assertEquals("linkedin", result?.name)
        Assert.assertEquals("/assets/wutsi/img/social/linkedin.png", result?.iconUrl)
        Assert.assertEquals("/me/settings?highlight=channels-container#channels", result?.url)
        Assert.assertEquals("This is title", result?.title)
    }
}
