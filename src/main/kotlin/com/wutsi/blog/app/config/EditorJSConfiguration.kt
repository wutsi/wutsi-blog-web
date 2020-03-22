package com.wutsi.blog.app.config

import com.wutsi.editorjs.html.EJSHtmlReader
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.editorjs.json.EJSJsonWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class EditorJSConfiguration {
    @Bean
    fun htmlWriter() = EJSHtmlWriter()

    @Bean
    fun htmlReader() = EJSHtmlReader()

    @Bean
    fun jsonReader() = EJSJsonReader()

    @Bean
    fun jsonWriter() = EJSJsonWriter()
}
