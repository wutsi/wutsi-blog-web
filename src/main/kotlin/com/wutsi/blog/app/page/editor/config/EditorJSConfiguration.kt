package com.wutsi.blog.app.page.editor.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.common.service.ImageKitService
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.editor.service.EJSFilterSet
import com.wutsi.blog.app.page.editor.service.filter.ButtonFilter
import com.wutsi.blog.app.page.editor.service.filter.ImageFilter
import com.wutsi.blog.app.page.editor.service.filter.LinkTargetFilter
import com.wutsi.editorjs.html.EJSHtmlReader
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.html.tag.TagProvider
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.editorjs.json.EJSJsonWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EditorJSConfiguration(
    private val objectMapper: ObjectMapper,
    private val requestContext: RequestContext,
    @Value("\${wutsi.base-url}") private val websiteUrl: String,
    @Value("\${wutsi.image.story.mobile.large.width}") private val mobileThumbnailLargeWidth: Int,
    @Value("\${wutsi.image.story.desktop.large.width}") private val desktopThumbnailLargeWidth: Int

) {
    @Bean
    fun htmlWriter() = EJSHtmlWriter(tagProvider())

    @Bean
    fun htmlReader() = EJSHtmlReader(tagProvider())

    @Bean
    fun jsonReader() = EJSJsonReader(objectMapper)

    @Bean
    fun jsonWriter() = EJSJsonWriter(objectMapper)

    @Bean
    fun tagProvider() = TagProvider()

    @Autowired
    @Bean
    fun ejsFilterSet(
        imageKitService: ImageKitService,
        requestContext: RequestContext
    ) = EJSFilterSet(
        arrayListOf(
            LinkTargetFilter(websiteUrl),
            ImageFilter(imageKitService, requestContext, desktopThumbnailLargeWidth, mobileThumbnailLargeWidth),
            ButtonFilter()
        )
    )
}
