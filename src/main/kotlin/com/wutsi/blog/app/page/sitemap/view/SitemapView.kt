package com.wutsi.blog.app.page.sitemap.view

import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.page.sitemap.model.SitemapModel
import com.wutsi.blog.app.page.sitemap.model.UrlModel
import com.wutsi.blog.app.page.sitemap.service.SitemapMapper
import com.wutsi.blog.client.SortOrder
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.StorySortStrategy
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.StorySummaryDto
import com.wutsi.blog.client.user.SearchUserRequest
import org.springframework.stereotype.Service
import org.springframework.web.servlet.View
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

@Service
class SitemapView(
        private val storyBackend: StoryBackend,
        private val userBackend: UserBackend,
        private val mapper: SitemapMapper
): View {
    companion object {
        const val LIMIT = 100
        const val MAX_URLS = 1000
    }

    override fun render(model: MutableMap<String, *>?, request: HttpServletRequest?, response: HttpServletResponse?) {
        response?.contentType = "application/xml"
        response?.characterEncoding = "utf-8"

        val sitemap = get()

        val jaxbContext = JAXBContext.newInstance(SitemapModel::class.java, UrlModel::class.java)
        val marshaller = jaxbContext.createMarshaller()
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        marshaller.marshal(sitemap, response?.outputStream)
    }

    private fun get(): SitemapModel {
        val urls = mutableListOf<UrlModel>()
        val stories = stories()

        urls.addAll(pageUrls())
        urls.addAll(storyUrls(stories))
        urls.addAll(userUrls(stories))
        return SitemapModel(
                url = urls
        )
    }

    private fun pageUrls(): List<UrlModel> = arrayListOf(
            mapper.toUrlModel("/"),
            mapper.toUrlModel("/about"),
            mapper.toUrlModel("/partner")
    )

    private fun storyUrls(stories: List<StorySummaryDto>): List<UrlModel> {
        return stories.map { mapper.toUrlModel(it) }
    }

    private fun userUrls(stories: List<StorySummaryDto>): List<UrlModel> {
        val userIds = stories.map { it.userId }.toSet()
        val users = userBackend.search(SearchUserRequest(
                userIds = userIds.toList()
        )).users
        return users.map { mapper.toUrlModel(it) }
    }

    private fun stories() : List<StorySummaryDto> {
        val stories = mutableListOf<StorySummaryDto>()
        while(true) {
            val tmp = storyBackend.search(SearchStoryRequest(
                    limit = LIMIT,
                    live = true,
                    status = StoryStatus.published,
                    sortBy = StorySortStrategy.modified,
                    sortOrder = SortOrder.descending
            )).stories
            stories.addAll(tmp)

            if (tmp.size < LIMIT || stories.size < MAX_URLS){
                break
            }
        }
        return stories
    }
}

