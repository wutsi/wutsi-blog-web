package com.wutsi.blog.app.model.ejs

data class EJSLinkMeta (
        val title: String = "",
        val description: String = "",
        val site_name: String = "",
        val image: EJSImageData = EJSImageData()
)
