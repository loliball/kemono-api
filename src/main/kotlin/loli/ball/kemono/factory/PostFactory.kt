package loli.ball.kemono.factory

import loli.ball.kemono.BASE_URL
import loli.ball.kemono.bean.*
import loli.ball.kemono.dateParser
import loli.ball.kemono.pictureCountRegexp
import loli.ball.kemono.rangeRegexp
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object PostFactory {

    /**
     * 拼接图片详情url
     * 请使用 SimplePost.url获取而不是本方法构造 除非用户主动输入
     * 例如：https://beta.kemono.party/fanbox/user/3115085/post/3197163
     *
     * @param service   服务器 ArtistService中的一个
     * @param user      作者id
     * @param id        作品id
     * @return 图片详情url
     */
    fun getPostUrlById(service: String, user: String, id: String) =
        "$BASE_URL/$service/user/$user/post/$id"

    fun getPostUrlBySimplePost(sp: SimplePost) =
        if (sp.service == ArtistService.discord.name) sp.url
        else getPostUrlById(sp.service, sp.user, sp.id)

    fun SimplePost.toPostUrl() = getPostUrlBySimplePost(this)

    /**
     * 获取全部画廊url
     *
     * @param offset 分页偏移，默认每页25个
     * @param search 搜索内容，根据画廊名称搜索
     * @return
     */
    fun allPosts(offset: Int = 0, search: String = "") =
        "$BASE_URL/posts?o=$offset" + if (search.isBlank()) "" else "&q=$search"

    /**
     * 解析画廊页面
     *
     * @param html 画廊页面html
     * @return
     */
    fun parseAllPost(html: String): SimplePostGroup {
        val doc = Jsoup.parse(html)
        val posts = mutableListOf<SimplePost>()
        val range = doc.select("#paginator-top > small").text().trim()
        val result = rangeRegexp.find(range)?.groupValues
        val now = result?.get(1)?.toInt() ?: 0
        val end = result?.get(2)?.toInt() ?: 0
        val all = result?.get(3)?.toInt() ?: 0

        val fs =
            doc.select("#main > section > div.card-list.card-list--legacy > div.card-list__items")
                .ifEmpty { null }?.get(0)
        for (fi in fs?.children().orEmpty()) {
            if (fi.tag().name == "article" ||
                fi.hasClass("post-card")
            ) {
                val id = fi.attr("data-id")
                val service = fi.attr("data-service")
                val user = fi.attr("data-user")

                val a = fi.child(0)
                val url = BASE_URL + a.attr("href").orEmpty()
                val name = a.select("header").ifEmpty { null }?.get(0)?.text().orEmpty()
                val thumbnail = a.select("div > img").ifEmpty { null }?.get(0)?.attr("src").orEmpty()
                val img = if (thumbnail.isBlank()) "" else BASE_URL + thumbnail
                val time = a.select("footer > time").ifEmpty { null }?.get(0)?.attr("datetime").orEmpty()
                val timestamp = if (time.isNotBlank()) dateParser.parse(time).time else 0
                val countText = a.select("footer > div").ifEmpty { null }?.get(0)?.text().orEmpty()
                val result1 = pictureCountRegexp.find(countText)?.groupValues
                val pictureCount = result1?.get(1)?.toInt() ?: 0

                posts += SimplePost(
                    service, user, id,
                    url, name, img, time, pictureCount, timestamp
                )
            }
        }

        return SimplePostGroup(posts, Range(now, end, all))
    }

    /**
     * 解析作品页面html
     *
     * @param html 作品详情页的html
     * @see getPostUrlById
     * @return
     */
    fun parsePost(html: String): Post {
        val doc = Jsoup.parse(html)
        doc.outputSettings(Document.OutputSettings().prettyPrint(false))

        var service = ""
        var user = ""
        var id = ""
        for (head in doc.select("head > meta")) {
            val content = head.attr("content")
            when (head.attr("name")) {
                "id" -> id = content
                "service" -> service = content
                "user" -> user = content
            }
        }
        val username = doc.select("#page > header > div.post__user > div:nth-child(3) > a").text()
        val userIcon = "$BASE_URL/icons/$service/$user"

        val name = doc.select("#page > header > div.post__info > h1.post__title > span:nth-child(1)").text()
        val time = doc.select("#page > header > div.post__info > div.post__published > time").attr("datetime")
        val desc = doc.select("#page > div > div.post__content").html().trim()
        val timestamp = if (time.isNotBlank()) dateParser.parse(time).time else 0

        val files = mutableListOf<RemoteFile>()
        val fs = doc.select("#page > div > ul.post__attachments").ifEmpty { null }?.get(0)
        for (fi in fs?.children().orEmpty()) {
            if (fi.hasClass("post__attachment")) {
                val a = fi.child(0)
                val fileUrl = BASE_URL + a.attr("href")
                val fileName = a.text().replaceFirst("Download ", "")
                files.add(RemoteFile(fileName, fileUrl))
            }
        }

        val pictures = mutableListOf<Picture>()
        val pics = doc.select("#page > div > div.post__files").ifEmpty { null }?.get(0)
        for (pic in pics?.children().orEmpty()) {
            if (pic.hasClass("post__thumbnail")) {
                val a = pic.child(0)
                val fullImage = BASE_URL + a.attr("href")
                val thumbnail = BASE_URL + a.child(0).attr("data-src")
                pictures.add(Picture(thumbnail, fullImage))
            }
        }

        val comments = mutableListOf<Comment>()
        val comm = doc.select("#page > footer > div.post__comments").ifEmpty { null }?.get(0)
        for (com in comm?.children().orEmpty()) {
            if (com.tag().name == "article" && com.hasClass("comment")) {
                val id1 = com.attr("id")
                val user1 = com.select("header").text()
                val message1 = com.select("section").text()
                val time1 = com.select("footer > time").attr("datetime")
                val ts1 = if (time.isNotBlank()) dateParser.parse(time).time else 0
                comments.add(Comment(id1, user1, message1, time1, ts1))
            }
        }
        return Post(service, user, id, username, userIcon, name, time, desc, files, pictures, comments, timestamp)
    }

}