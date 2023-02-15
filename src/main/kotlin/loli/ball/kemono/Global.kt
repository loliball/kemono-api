package loli.ball.kemono

import java.text.SimpleDateFormat
import java.util.*

const val BASE_URL = "https://kemono.party"

//Mon, 02 Aug 2021 01:35:16 GMT
val GTMDateParser = object : SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH) {
    override fun parse(source: String?): Date {
        if (source.isNullOrBlank()) return Date(0L)
        return try {
            super.parse(source)
        } catch (e: Exception) {
            Date(0)
        }
    }
}

//2022-01-26 22:10:13
val dateParser = object : SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH) {
    override fun parse(source: String?): Date {
        if (source.isNullOrBlank()) return Date(0L)
        return try {
            super.parse(source)
        } catch (e: Exception) {
            Date(0)
        }
    }
}

val rangeRegexp = """Showing (\d+) - (\d+) of (\d+)""".toRegex()

val pictureCountRegexp = """(\d+).*""".toRegex()

class AccountException(override val message: String?) : Exception(message)