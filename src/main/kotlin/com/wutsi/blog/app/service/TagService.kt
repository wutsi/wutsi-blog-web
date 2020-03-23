package com.wutsi.blog.app.service

import com.wutsi.blog.app.backend.TagBackend
import com.wutsi.blog.app.mapper.TagMapper
import com.wutsi.blog.app.model.TagListModel
import org.springframework.stereotype.Service

@Service
class TagService(
        private val backend: TagBackend,
        private val mapper: TagMapper
) {
    fun search(query: String) : TagListModel {
        val tags = backend.search(query).tags
        return TagListModel( results = tags.map { mapper.toTagModel(it) } )
    }
}

