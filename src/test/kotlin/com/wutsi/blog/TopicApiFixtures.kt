package com.wutsi.blog

import com.wutsi.blog.client.story.TopicDto

object TopicApiFixtures {
    fun createTopicDto(id:Long, name:String, parentId: Long = -1) = TopicDto(
            id = id,
            name = name,
            parentId = parentId
    )
}
