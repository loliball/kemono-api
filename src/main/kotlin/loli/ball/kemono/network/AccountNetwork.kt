package loli.ball.kemono.network

import loli.ball.kemono.AccountException
import loli.ball.kemono.BASE_URL
import loli.ball.kemono.bean.*
import loli.ball.kemono.factory.AccountFactory
import loli.ball.kemono.network.Network.toArtist
import loli.ball.kemono.network.Network.toPost
import okhttp3.Cookie
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.EMPTY_REQUEST

@Suppress("unused")
object AccountNetwork {

    private var noRedirectsClient = OkHttpClient.Builder()
        .followRedirects(false)
//        .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 8880))) // fiddler
//        .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("127.0.0.1", 10800))) // for debug
        .build()

    private var account: Account? = null

    /**
     * 设置账户信息
     * 在调用此方法之前请务必使用
     * Account.isExpired 验证cookie是否过期
     *
     * @param account 账户 通常为本地序列化的缓存
     * @see Account.isExpired
     */
    fun setAccount(account: Account) {
        this.account = account
    }

    /**
     * 没有特殊需求请勿设置
     * 必须设置followRedirects(false)
     *
     * @param customClient 自定义的请求器
     */
    fun setClient(customClient: OkHttpClient) {
        noRedirectsClient = customClient
    }

    /**
     * 获取所有收藏的画师
     *
     * @return 所有作者的摘要信息
     */
    fun favoriteArtists() = AccountFactory.parseFavoriteArtist(favoriteArtistsJson())

    /**
     * 获取所有收藏的作品
     *
     * @return 所有作品的摘要信息
     */
    fun favoritePosts() = AccountFactory.parseFavoritePost(favoritePostsJson())

    fun favoriteArtist(service: String, user: String) {
        val request = Request.Builder()
            .url("$BASE_URL/favorites/artist/$service/$user")
            .addHeader("cookie", account!!.cookie)
            .post(EMPTY_REQUEST)
            .build()
        noRedirectsClient.newCall(request).execute()
    }

    fun unFavoriteArtist(service: String, user: String) {
        val request = Request.Builder()
            .url("$BASE_URL/favorites/artist/$service/$user")
            .addHeader("cookie", account!!.cookie)
            .delete()
            .build()
        noRedirectsClient.newCall(request).execute()
    }

    fun favoritePost(service: String, user: String, id: String) {
        val request = Request.Builder()
            .url("$BASE_URL/favorites/post/$service/$user/$id")
            .addHeader("cookie", account!!.cookie)
            .post(EMPTY_REQUEST)
            .build()
        noRedirectsClient.newCall(request).execute()
    }

    fun unFavoritePost(service: String, user: String, id: String) {
        val request = Request.Builder()
            .url("$BASE_URL/favorites/post/$service/$user/$id")
            .addHeader("cookie", account!!.cookie)
            .delete()
            .build()
        noRedirectsClient.newCall(request).execute()
    }

    fun Artist.favorite() = favoriteArtist(service, user)

    fun Artist.unFavorite() = unFavoriteArtist(service, user)

    fun FavoriteArtistItem.favorite() = favoriteArtist(service, id)

    fun FavoriteArtistItem.unFavorite() = unFavoriteArtist(service, id)

    fun Post.favorite() = favoritePost(service, user, id)

    fun Post.unFavorite() = unFavoritePost(service, user, id)

    fun FavoritePostItem.favorite() = favoritePost(service, user, id)

    fun FavoritePostItem.unFavorite() = unFavoritePost(service, user, id)

    fun FavoriteArtistItem.toSimpleArtist() =
        SimpleArtist(id, indexed, name, service, updated, indexedTimestamp, updatedTimestamp, icon)

    fun FavoriteArtistItem.toArtist() =
        this.toSimpleArtist().toArtist()

    fun FavoritePostItem.toSimplePost(): SimplePost {
        val url = "$BASE_URL/$service/user/$user/post/$id"
        val pictureCount = attachments.size
        return SimplePost(service, user, id, url, title, file.path, published, pictureCount, publishedTimestamp)
    }

    fun FavoritePostItem.toPictureGroup() =
        this.toSimplePost().toPost()

    fun favoriteArtistsJson(): String {
        val request = Request.Builder()
            .url("$BASE_URL/api/favorites?type=artist")
            .addHeader("cookie", account!!.cookie)
            .get()
            .build()
        val response = noRedirectsClient.newCall(request).execute()
        if(response.code != 200) {
            throw AccountException(response.code.toString())
        }
        return response.body?.string().orEmpty()
    }

    fun favoritePostsJson(): String {
        val request = Request.Builder()
            .url("$BASE_URL/api/favorites?type=post")
            .addHeader("cookie", account!!.cookie)
            .get()
            .build()
        val response = noRedirectsClient.newCall(request).execute()
        if(response.code != 200) {
            throw AccountException(response.code.toString())
        }
        return response.body?.string().orEmpty()
    }

    /**
     * 注册
     * 成功后会自动设置account
     * 无需调用 login 或 setAccount
     *
     * @param username 用户名
     * @param password 密码
     * @return 成功时返回Account 否则返回null
     */
    fun register(username: String, password: String): Account? {
        val body = FormBody.Builder()
            .add("favorites", "")
            .add("username", username)
            .add("password", password)
            .add("confirm_password", password)
            .build()
        val request = Request.Builder()
            .url("$BASE_URL/account/register")
            .post(body)
            .build()
        val response = noRedirectsClient.newCall(request).execute()
        val cookie = response.header("set-cookie")
        return if (response.code == 302 &&
            response.header("location") == "/artists?logged_in=yes"
        ) {
            val cookie1 = Cookie.parse(
                BASE_URL.toHttpUrl(),
                cookie.orEmpty()
            ) ?: return null
            val cook = "${cookie1.name}=${cookie1.value}"
            val time = cookie1.expiresAt
            account = Account(username, password, cook, time)
            account
        } else {
            null
        }
    }

    /**
     * 登录
     * 成功后会自动设置account
     * 无需调用setAccount
     *
     * @param username 用户名
     * @param password 密码
     * @return 成功时返回Account 否则返回null
     */
    fun login(username: String, password: String): Account? {
        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()
        val request = Request.Builder()
            .url("$BASE_URL/account/login")
            .post(body)
            .build()
        val response = noRedirectsClient.newCall(request).execute()
        val cookie = response.header("set-cookie")
        return when (response.header("location")) {
            "/artists?logged_in=yes" -> {
                val cookie1 = Cookie.parse(
                    BASE_URL.toHttpUrl(),
                    cookie.orEmpty()
                ) ?: return null
                val cook = "${cookie1.name}=${cookie1.value}"
                val time = cookie1.expiresAt
                account = Account(username, password, cook, time)
                account
            }
            "/account/login" -> null
            else -> null
        }
    }
}