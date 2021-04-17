package com.wutsi.blog.config

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.wutsi.stream.Event
import com.wutsi.stream.EventHandler
import com.wutsi.stream.EventStream
import com.wutsi.stream.rabbitmq.RabbitMQEventStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.ExecutorService

@Configuration
@Profile("aws")
class MQueueRemoteConfiguration(
    @Autowired
    private val eventPublisher: ApplicationEventPublisher,
    @Value(value = "\${rabbitmq.url}")
    private val url: String,
    @Value(value = "\${rabbitmq.thread-pool-size}")
    private val threadPoolSize: Int,
    @Value(value = "\${rabbitmq.max-retries}")
    private val maxRetries: Int,
    @Value(value = "\${rabbitmq.queue-ttl-seconds}")
    private val queueTtlSeconds: Long
) {
    @Bean
    public fun connectionFactory(): ConnectionFactory {
        val factory = ConnectionFactory()
        factory.setUri(url)
        return factory
    }

    @Bean(destroyMethod = "shutdown")
    public fun executorService(): ExecutorService =
        java.util.concurrent.Executors.newFixedThreadPool(threadPoolSize)

    @Bean(destroyMethod = "close")
    public fun channel(): Channel = connectionFactory()
        .newConnection(executorService())
        .createChannel()

    @Bean(destroyMethod = "close")
    fun eventStream(): EventStream = RabbitMQEventStream(
        name = "wutsi-blog-web",
        channel = channel(),
        queueTtlSeconds = queueTtlSeconds,
        maxRetries = maxRetries,
        handler = object : EventHandler {
            override fun onEvent(event: Event) {
                eventPublisher.publishEvent(event)
            }
        }
    )

    @Bean
    public fun rabbitMQHealthIndicator(): HealthIndicator =
        com.wutsi.stream.rabbitmq.RabbitMQHealthIndicator(channel())

    @Scheduled(cron = "\${rabbitmq.replay-cron}")
    public fun replayDlq() {
        (eventStream() as com.wutsi.stream.rabbitmq.RabbitMQEventStream).replayDlq()
    }
}
