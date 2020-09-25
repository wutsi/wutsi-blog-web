package com.wutsi.blog.app.page.story.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.page.editor.service.EJSFilterSet
import com.wutsi.blog.app.page.editor.service.filter.ImageKitFilter
import com.wutsi.blog.app.page.editor.service.filter.ImageLozadFilter
import com.wutsi.blog.app.page.editor.service.filter.LinkTargetFilter
import com.wutsi.blog.app.page.editor.service.filter.LinkUTMFilter
import com.wutsi.blog.app.page.story.service.HtmlImageService
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
        @Value("\${wutsi.base-url}") private val websiteUrl: String
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
            imageSize: HtmlImageService
    ) = EJSFilterSet(arrayListOf(
            linkFilter(),
            LinkUTMFilter(),
            ImageKitFilter(imageSize),
            ImageLozadFilter()  /* should be the LAST image filter */
    ))

    @Bean
    fun linkFilter() = LinkTargetFilter(websiteUrl)
}
