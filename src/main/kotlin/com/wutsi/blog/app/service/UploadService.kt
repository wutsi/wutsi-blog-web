package com.wutsi.blog.app.service

import com.wutsi.blog.app.model.File
import com.wutsi.blog.app.model.UploadModel
import com.wutsi.core.service.StorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.Date
import java.util.TimeZone
import java.util.UUID


@Service
class UploadService(
        private val clock: Clock,
        private val storage: StorageService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadService::class.java)
    }

    fun upload(file: MultipartFile): UploadModel {
        val path = key(file)
        val url = storage.store(path, file.inputStream, file.contentType)

        LOGGER.info("Store ${file.originalFilename} to ${url}")
        return UploadModel(
                result = 1,
                file = File(url = url)
        )
    }

    private fun key(file: MultipartFile): String {
        val fmt = SimpleDateFormat("yyyy/MM/dd/HH")
        fmt.timeZone = TimeZone.getTimeZone("UTC")

        val prefix = fmt.format(Date(clock.millis()))
        return "upload/$prefix/${UUID.randomUUID()}-${file.originalFilename}"
    }

}
