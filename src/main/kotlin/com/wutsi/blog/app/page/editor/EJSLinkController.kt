package com.wutsi.blog.app.page.editor

import com.wutsi.blog.app.page.editor.model.EJSImageData
import com.wutsi.blog.app.page.editor.model.EJSLinkMeta
import com.wutsi.blog.app.page.editor.model.EJSLinkResponse
import com.wutsi.extractor.DescriptionExtractor
import com.wutsi.extractor.Downloader
import com.wutsi.extractor.ImageExtractor
import com.wutsi.extractor.SiteNameExtractor
import com.wutsi.extractor.TitleExtractor
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URL

@Controller
@RequestMapping("/ejs/link")
class EJSLinkController(
        private val downloader: Downloader,
        private val imageExtractor: ImageExtractor,
        private val titleExtractor: TitleExtractor,
        private val descriptionExtractor: DescriptionExtractor,
        private val siteNameExtractor: SiteNameExtractor
) {

    @ResponseBody
    @GetMapping(value=["/fetch"], produces = ["application/json"])
    fun fetch(@RequestParam url: String): EJSLinkResponse {
        val html = downloader.download(URL(url))
        return EJSLinkResponse(
                success = 1,
                meta = EJSLinkMeta(
                        title = nullToEmpty(titleExtractor.extract(html)),
                        description = nullToEmpty(descriptionExtractor.extract(html)),
                        site_name = nullToEmpty(siteNameExtractor.extract(URL(url), html)),
                        image = EJSImageData(
                                url = nullToEmpty(imageExtractor.extract(html))
                        )
                )
        )
    }

    private fun nullToEmpty(value: String?) = if (value == null) "" else value
}
