package loli.ball.kemono.bean

import kotlinx.serialization.Serializable

//作者的摘要数据(首页全部作者列表)
@Serializable
data class SimpleArtist(
    val id: String,                     //作者id
    val indexed: Float = 0f,            //创建日期
    val name: String = "",              //作者名称
    val service: String,                //隶属于的服务器 详见ArtistService
    val updated: Float = 0f,            //更新日期
    //以下内容为解析、计算获得
    var indexedTimestamp: Long = 0,     //创建时间戳
    var updatedTimestamp: Long = 0,     //更新时间戳
    var icon: String = "",              //作者头像
    // 2022-08-18 新增字段
    val favorited: Int = 0,             //收藏人数
)

//作者概述 包含首页缩略图
data class Artist(
    val service: String,            //隶属于的服务器 详见ArtistService
    val user: String,               //作者的id 与正版服务器上的id相同
    val name: String,               //作者名称
    val origin: String,             //正版购买链接
    val icon: String,               //作者头像
    val title: String,              //网页标题
    val range: Range,               //缩略图的范围
    val simplePosts: List<SimplePost> //缩略图
)

data class Range(
    val now: Int,   //本页开始 从1开始(offset+1)
    val end: Int,   //本页结束
    val all: Int    //所有作品，包括不在本次请求中的
)

enum class ArtistService(val url: String) {
    fantia("https://fantia.jp"),
    discord("https://discord.com"),
    dlsite("https://www.dlsite.com"),
    subscribestar("https://www.subscribestar.com"),
    gumroad("https://gumroad.com"),
    fanbox("https://www.fanbox.cc"),
    patreon("https://www.patreon.com")
}
