package loli.ball.kemono.bean

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import okhttp3.Cookie

@Serializable
data class Account(
    val username: String,               //用户名
    val password: String,               //密码
    val cookie: String,                 //cookie 有效期一个月
    val expiresTimestamp: Long          //cookie过期时间戳
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiresTimestamp
    }
}

@Serializable
data class FavoriteArtistItem(
    val faved_seq: Int,                 //收藏的顺序
    val id: String,                     //作者id
    val indexed: String,                //创建日期
    val name: String,                   //作者名称
    val service: String,                //隶属于的服务器 详见ArtistService
    val updated: String,                //更新日期

    var indexedTimestamp: Long = 0,     //创建时间戳
    var updatedTimestamp: Long = 0,     //更新时间戳
    var icon: String = ""               //作者头像
)

@Serializable
data class FavoritePostItem(
    val added: String,                  //收藏时间
    val attachments: List<Attachment>,  //收藏的附件 可能为空
    val content: String,                //作品简介html
    val edited: String,                 //编辑时间
//    val embed: Any? = null,           //ignore
    val faved_seq: Int,                 //收藏的顺序
    val file: Attachment,               //收藏的附件 封面预览图
    val id: String,                     //作品的id
    val published: String,              //发布时间
    val service: String,                //隶属于的服务器 详见ArtistService
    val shared_file: Boolean,           //未知 通常是false
    val title: String,                  //作品的标题
    val user: String,                   //作者id

    var editedTimestamp: Long = 0,      //编辑时间戳
    var publishedTimestamp: Long = 0,   //发布时间戳
)

//通常是图片
@Serializable
data class Attachment(
    val name: String,                   //附件名称
    val path: String                    //附件url
)
