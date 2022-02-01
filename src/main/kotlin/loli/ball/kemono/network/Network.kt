package loli.ball.kemono.network

import loli.ball.kemono.bean.SimpleArtist
import loli.ball.kemono.bean.SimplePost
import loli.ball.kemono.factory.ArtistFactory
import loli.ball.kemono.factory.ArtistFactory.toArtistUrl
import loli.ball.kemono.factory.PostFactory
import loli.ball.kemono.factory.PostFactory.toPostUrl
import okhttp3.OkHttpClient
import okhttp3.Request

object Network {

    private var client: OkHttpClient = OkHttpClient()

    private fun request(url: String) =
        Request.Builder()
            .url(url)
            .get()
            .build()

    private fun String.toGetRequest() = request(this)

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
    fun doRequest(url: String) =
        url.toGetRequest().call()

    fun allArtistJson() = ArtistFactory.allArtist().toGetRequest().call()

    /**
     * 获取全部的作者
     *
     * @return 全部作者的摘要信息
     */
    fun allArtist() =
        ArtistFactory.parseAllArtist(allArtistJson().orEmpty())

    fun SimpleArtist.toArtist(offset: Int = 0) =
        ArtistFactory.parseArtist(this.toArtistUrl(offset).toGetRequest().call().orEmpty())

    fun SimplePost.toPost() =
        PostFactory.parsePost(this.toPostUrl().toGetRequest().call().orEmpty())

}
