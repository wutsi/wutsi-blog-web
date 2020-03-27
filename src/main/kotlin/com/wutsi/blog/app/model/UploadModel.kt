package com.wutsi.blog.app.model

data class UploadModel(
        val result: Int = 1,
        val file: File = File ()
)

data class File (
        val url: String = "",
        val width: Int = 512,
        val height: Int = 512,
        val withBorder: Boolean = false,
        val withBackground: Boolean = false,
        val stretched: Boolean = false,
        val caption: String = ""
)
