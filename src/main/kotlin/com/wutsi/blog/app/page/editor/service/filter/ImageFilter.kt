package com.wutsi.blog.app.page.editor.service.filter

import com.wutsi.blog.app.common.service.ImageKitService
import com.wutsi.blog.app.common.service.RequestContext
import com.wutsi.blog.app.page.editor.service.Filter
import com.wutsi.blog.app.page.story.service.HtmlImageService
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ImageFilter(
    private val sizes: HtmlImageService,
    private val imageKitService: ImageKitService,
    private val requestContext: RequestContext,
    private val mobileThumbnailLargeWidth: Int
) : Filter {
    companion object {
        const val LARGE_IMAGE_SIZE = 960
    }

    override fun filter(html: Document) {
        html.select("img")
            .forEach {
                try {
                    filter(it)
                } catch (ex: Exception) {
                }
            }
    }

    private fun filter(img: Element) {
        val url = img.attr("src")
        val width = attrAsInt(img, "width")
        val height = attrAsInt(img, "height")

        img.attr("loading", "lazy")
        if (requestContext.isMobileUserAgent()) {
            if (width > mobileThumbnailLargeWidth) {
                val xurl = imageKitService.transform(url, width = mobileThumbnailLargeWidth.toString())
                img.attr("src", xurl)
                img.attr("width", mobileThumbnailLargeWidth.toString())
                img.removeAttr("height")
            }
        } else {
            if (width > LARGE_IMAGE_SIZE || height > LARGE_IMAGE_SIZE) {
                val srcset = sizes.srcset(url)
                if (srcset.isNotEmpty()) {
                    img.attr("srcset", srcset)
                    img.attr("sizes", sizes.sizes())
                }
            }
        }
    }

    private fun attrAsInt(elt: Element, name: String): Int {
        try {
            return elt.attr(name).toInt()
        } catch (ex: Exception) {
            return 0
        }
    }
}
