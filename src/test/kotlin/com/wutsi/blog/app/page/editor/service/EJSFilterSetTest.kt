package com.wutsi.blog.app.page.editor.service

import org.jsoup.Jsoup
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class EJSFilterSetTest {
    @Mock
    private lateinit var filter1: Filter

    @Mock
    private lateinit var filter2: Filter

    @Test
    fun filter() {
        val set = EJSFilterSet(arrayListOf(filter1, filter2))
        val doc = Jsoup.parse("<body></body>")
        set.filter(doc)

        verify(filter1).filter(doc)
        verify(filter2).filter(doc)
    }
}
