package com.wutsi.blog.app.service

import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date


@Service
class Moment(
        private val clock: Clock,
        private val localization: LocalizationService
) {

    fun format(date: Date?): String {
        if (date == null) {
            return ""
        }

        val now = toLocalDateTime(Date(clock.millis()))
        val localDate = toLocalDateTime(date)
        val minutes = ChronoUnit.MINUTES.between(now, localDate)
        val hours = ChronoUnit.HOURS.between(now, localDate)
        val days = ChronoUnit.DAYS.between(now, localDate)
        val months = ChronoUnit.MONTHS.between(now, localDate)
        val years = ChronoUnit.MONTHS.between(now, localDate)

        if (minutes == 0L){
            return getMessage("moment.now")
        } else if (Math.abs(minutes) < 60) {
            return if (minutes < 0) getMessage("moment.ago_minutes", arrayOf(-minutes)) else getMessage("moment.in_minutes", arrayOf(minutes))
        } else if (Math.abs(hours) < 24) {
            return if (hours < 0) getMessage("moment.ago_hours", arrayOf(-hours)) else getMessage("moment.in_hours", arrayOf(hours))
        } else if (days == 0L) {
            return getMessage("moment.today")
        } else if (days == -1L){
            return getMessage("moment.yesterday")
        } else if (months == 0L) {
            return if (days < 0) getMessage("moment.ago_days", arrayOf(-days)) else getMessage("moment.in_days", arrayOf(days))
        } else if (Math.abs(months) < 12) {
            return if (months < 0) getMessage("moment.ago_months", arrayOf(-months)) else getMessage("moment.in_months", arrayOf(months))
        } else {
            return if (years < 0) getMessage("moment.ago_years", arrayOf(-years)) else getMessage("moment.in_years", arrayOf(years))
        }
    }

    private fun toLocalDateTime(date: Date) : LocalDateTime {
        return  date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime()
    }

    private fun getMessage(key: String, args: Array<Any>? = null): String {
        return localization.getMessage(key, args)
    }
}
