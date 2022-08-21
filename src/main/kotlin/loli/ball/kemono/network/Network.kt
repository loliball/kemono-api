@file:Suppress("unused")
package loli.ball.kemono.network

import loli.ball.kemono.bean.SimpleArtist
import loli.ball.kemono.bean.SimplePost
import loli.ball.kemono.bean.SimplePostGroup
import loli.ball.kemono.factory.ArtistFactory
import loli.ball.kemono.factory.ArtistFactory.toArtistUrl
import loli.ball.kemono.factory.PostFactory
import loli.ball.kemono.factory.PostFactory.toPostUrl
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request

object Network {

    private var client: OkHttpClient = OkHttpClient()

    private fun String.toGetRequest(noCache: Boolean = false) = Request.Builder()
        .url(this)
        .let {
            if(noCache) it.cacheControl(CacheControl.FORCE_NETWORK)
            else it
        }
        .get()
        .build()

    private fun Request.call() = client.newCall(this).execute().body?.string()

    /**
     * 没有特殊需求请勿设置
     *
     * @param customClient 自定义的请求器
     */
    fun setClient(customClient: OkHttpClient) {
        client = customClient
    }

    /**
     * 简易GET请求
     *
     * @param url 请求的链接
     * @return 响应体的String形式
     */
    fun doRequest(url: String, noCache: Boolean = false) =
        url.toGetRequest(noCache).call()

    /**
     * 获取全部的作者
     *
     * @return 全部作者的摘要信息
     */
    fun allArtist(noCache: Boolean = false): List<SimpleArtist> =
        ArtistFactory.parseAllArtist(ArtistFactory.allArtist().toGetRequest(noCache).call().orEmpty())

    /**
     * 获取全部画廊
     *
     * @param offset 分页偏移，默认每页25个
     * @param search 搜索内容，根据画廊名称搜索
     * @return
     */
    fun allPosts(offset: Int = 0, search: String = "", noCache: Boolean = false): SimplePostGroup =
        PostFactory.parseAllPost(PostFactory.allPosts(offset, search).toGetRequest(noCache).call().orEmpty())

    fun SimpleArtist.toArtist(offset: Int = 0, noCache: Boolean = false) =
        ArtistFactory.parseArtist(this.toArtistUrl(offset).toGetRequest(noCache).call().orEmpty())

    fun SimplePost.toPost(noCache: Boolean = false) =
        PostFactory.parsePost(this.toPostUrl().toGetRequest(noCache).call().orEmpty())

}
