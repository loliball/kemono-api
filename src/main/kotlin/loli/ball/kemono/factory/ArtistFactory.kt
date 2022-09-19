package loli.ball.kemono.factory

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import loli.ball.kemono.*
import loli.ball.kemono.bean.Artist
import loli.ball.kemono.bean.Range
import loli.ball.kemono.bean.SimpleArtist
import loli.ball.kemono.bean.SimplePost
import org.jsoup.Jsoup

object ArtistFactory {

    /**
     * 该url可以获得全部作者的json数据
     * 请求获得的数据量十分庞大，需缓存
     *
     * @return 全部作者的json数据url
     */
    fun allArtist() = "$BASE_URL/api/creators"

    /**
     *  拼接作者页url
     *  例如：https://beta.kemono.party/fanbox/user/3115085?o=0
     *
     * @param service   服务器 ArtistService中的一个
     * @param user      作者id
     * @param offset    翻页偏移量
     * @return 作者页url
     */
    fun getArtistUrlById(service: String, user: String, offset: Int = 0) =
        "$BASE_URL/$service/user/$user?o=$offset"

    /**
     * 通过作者摘要数据获取作者详细信息，包括部分作品预览
     *
     * @param simpleArtist  首页作者摘要数据
     * @param offset        翻页偏移量
     * @see parseAllArtist
     * @return 作者详细信息url
     */
    fun getArtistUrlBySimpleArtist(simpleArtist: SimpleArtist, offset: Int = 0) =
        getArtistUrlById(simpleArtist.service, simpleArtist.id, offset)

    /**
     * @see getArtistUrlBySimpleArtist
     * @param offset
     * @return
     */
    fun SimpleArtist.toArtistUrl(offset: Int = 0) =
        getArtistUrlBySimpleArtist(this, offset)


    /**
     * 解析json为实体类SimpleArtist
     * 额外的工作:
     * 解析时间到时间戳
     * 计算作者头像url
     *
     * @param json 待解析的json
     * @see allArtist
     * @return 包含作者摘要数据的列表
     */
    fun parseAllArtist(json: String): List<SimpleArtist> {
        val artistList = Json.decodeFromString<List<SimpleArtist>>(json)
        artistList.forEach {
            try {
                it.indexedTimestamp = it.indexed.toLong() * 1000L
                it.updatedTimestamp = it.updated.toLong() * 1000L
                it.icon = "$BASE_URL/icons/${it.service}/${it.id}"
            } catch (e: Exception) {
                println("error parse $it")
                e.printStackTrace()
            }
        }
        return artistList
    }



    /**
     * 解析作者页面html
     *
     * @param html  作者页面的html字符串
     * @see getArtistUrlById
     * @return 作者页面的基本信息
     */
    fun parseArtist(html: String): Artist {
        val doc = Jsoup.parse(html)

        var id = ""
        var service = ""
        var name = ""
        for (head in doc.select("head > meta")) {
            val content = head.attr("content")
            when (head.attr("name")) {
                "id" -> id = content
                "service" -> service = content
                "artist_name" -> name = content
            }
        }
        val origin = doc.select("#user-header__info-top > a").attr("href")
        val icon = BASE_URL + doc.select("#main > section > header > a > picture > img").attr("src")
        val range = doc.select("#paginator-top > small").text().trim()
        val result = rangeRegexp.find(range)?.groupValues
        val now = result?.get(1)?.toInt() ?: 0
        val end = result?.get(2)?.toInt() ?: 0
        val all = result?.get(3)?.toInt() ?: 0

        val simplePosts = mutableListOf<SimplePost>()
        val pictures =
            doc.select("#main > section > div.card-list.card-list--legacy > div.card-list__items")
                .ifEmpty { null }?.get(0)
        for (art in pictures?.children().orEmpty()) {
            if (art.tag().name == "article") {
                val dataId = art.attr("data-id")
                val dataService = art.attr("data-service")
                val user = art.attr("data-user")
                val url = BASE_URL + art.child(0).attr("href")
                val title = art.select("a > header").text()
                val imageUrl = art.select("a > div > img").ifEmpty { null }
                val img = if (imageUrl == null) "" else BASE_URL + imageUrl.attr("src")
                val time = art.select("a > footer > time").attr("datetime")
                val pictureCountStr = art.select("a > footer > div").text().trim()
                val timestamp = if (time.isNotBlank()) dateParser.parse(time).time else 0
                val result1 = pictureCountRegexp.find(pictureCountStr)?.groupValues
                val pictureCount = result1?.get(1)?.toInt() ?: 0

                simplePosts.add(SimplePost(dataService, user, dataId, url, title, img, time, pictureCount, timestamp))
            }
        }
        return Artist(service, id, name, origin, icon, doc.title(), Range(now, end, all), simplePosts)
    }

}
