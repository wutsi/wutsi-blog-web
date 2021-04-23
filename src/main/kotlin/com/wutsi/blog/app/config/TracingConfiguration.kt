package com.wutsi.blog.app.config

import com.wutsi.tracing.DynamicTracingContext
import com.wutsi.tracing.TracingContext
import com.wutsi.tracing.TracingFilter
import com.wutsi.tracing.TracingRequestInterceptor
import feign.RequestInterceptor
import org.springframework.beans.factory.`annotation`.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.`annotation`.Bean
import org.springframework.context.`annotation`.Configuration
import javax.servlet.Filter

@Configuration
public class TracingConfiguration(
    @Autowired
    private val context: ApplicationContext
) {
    @Bean
    public fun tracingFilter(): Filter = TracingFilter(tracingContext())

    @Bean
    public fun tracingContext(): TracingContext = DynamicTracingContext(context)

    @Bean
    public fun tracingRequestInterceptor(): RequestInterceptor =
        TracingRequestInterceptor("wutsi-blog-web", tracingContext())
}
