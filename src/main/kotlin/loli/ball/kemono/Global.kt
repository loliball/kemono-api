package loli.ball.kemono

import java.text.SimpleDateFormat
import java.util.*

const val BASE_URL = "https://beta.kemono.party"

//Mon, 02 Aug 2021 01:35:16 GMT
val GTMDateParser = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)

//2022-01-26 22:10:13
val dateParser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

val rangeRegexp = """Showing (\d+) - (\d+) of (\d+)""".toRegex()

val pictureCountRegexp = """(\d+).*""".toRegex()

class AccountException(override val message: String?): Exception(message)