package com.wutsi.blog.app.page.story.service

import com.wutsi.blog.app.backend.TagBackend
import com.wutsi.blog.app.page.story.model.TagModel
import org.springframework.stereotype.Service

@Service
class TagService(
        private val backend: TagBackend,
        private val mapper: TagMapper
) {
    fun search(query: String) : List<TagModel> {
        val tags = backend.search(query).tags
        return tags.map { mapper.toTagModel(it) }
    }
}

