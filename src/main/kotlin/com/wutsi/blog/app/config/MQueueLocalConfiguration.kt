package com.wutsi.blog.config

import com.wutsi.email.event.EmailEventStream
import com.wutsi.stream.Event
import com.wutsi.stream.EventHandler
import com.wutsi.stream.EventStream
import com.wutsi.stream.file.FileEventStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.io.File

@Configuration
@Profile("!aws")
class MQueueLocalConfiguration(
    @Autowired
    private val eventPublisher: ApplicationEventPublisher
) {
    @Bean(name = ["emailEventStream"], destroyMethod = "close")
    fun emailEventStream(): EventStream = eventStream(
        name = EmailEventStream.NAME
    )

    private fun eventStream(name: String): EventStream = FileEventStream(
        name = name,
        root = File(
            System.getProperty("user.home") + File.separator + "tmp",
            "mqueue"
        ),
        handler = object : EventHandler {
            override fun onEvent(event: Event) {
                eventPublisher.publishEvent(event)
            }
        }
    )
}
