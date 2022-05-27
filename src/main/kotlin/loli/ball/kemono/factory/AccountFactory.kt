package loli.ball.kemono.factory

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import loli.ball.kemono.BASE_URL
import loli.ball.kemono.GTMDateParser
import loli.ball.kemono.bean.FavoriteArtistItem
import loli.ball.kemono.bean.FavoritePostItem

object AccountFactory {

    /**
     * 解析json为实体类FavoriteArtistItem
     * 额外的工作:
     * 解析时间到时间戳
     * 计算作者头像url
     *
     * @param json 待解析的json
     * @return 包含作者摘要数据的列表
     */
    fun parseFavoriteArtist(json: String): List<FavoriteArtistItem> {
        val list = Json.decodeFromString<List<FavoriteArtistItem>>(json)
        list.forEach {
            try {
                it.indexedTimestamp = GTMDateParser.parse(it.indexed).time
                it.updatedTimestamp = GTMDateParser.parse(it.updated).time
                it.icon = "$BASE_URL/icons/${it.service}/${it.id}"
            } catch (e: Exception) {
                println("error parse $it")
                e.printStackTrace()
            }
        }
        return list
    }

    private val postParser = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun parseFavoritePost(json: String): List<FavoritePostItem> {
        val list = postParser.decodeFromString<List<FavoritePostItem>>(json)
        list.forEach {
            try {
                it.editedTimestamp = GTMDateParser.parse(it.edited).time
                it.publishedTimestamp = GTMDateParser.parse(it.published).time
            } catch (e: Exception) {
                println("error parse $it")
                e.printStackTrace()
            }
        }
        return list
    }

}