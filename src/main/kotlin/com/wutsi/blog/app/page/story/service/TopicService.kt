package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.page.story.model.TopicModel
import com.wutsi.blog.sdk.TopicApi
import org.springframework.stereotype.Service

@Service
class TopicService(
        private val api: TopicApi,
        private val mapper: TopicMapper
) {
    private var topics: List<TopicModel>? = null

    fun all() : List<TopicModel> {
        if (topics == null) {
            topics = api.all().topics.map { mapper.toTopicMmodel(it) }
        }
        return topics!!
    }

    fun get(id: Long) = all().find { it.id == id }
}

