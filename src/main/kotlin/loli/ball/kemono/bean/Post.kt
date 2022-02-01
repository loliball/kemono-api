package loli.ball.kemono.bean

//作品摘要
//作者页面的每一期作品
data class SimplePost(
    val service: String,            //隶属于的服务器 详见ArtistService
    val user: String,               //作者的id
    val id: String,                 //图片组id
    val url: String = "",           //详情链接
    val title: String = "",         //作品的标题
    val img: String = "",           //预览图链接
    val time: String = "",          //上传时间 yyyy-MM-dd HH:mm:ss
    val pictureCount: Int = 0,      //本期作品包含的图片数量 封面不算
    val timestamp: Long = 0         //上传时间戳
)

//作品详情页面
data class Post(
    val service: String,            //隶属于的服务器 详见ArtistService
    val user: String,               //作者的id
    val id: String,                 //图片组id
    val username: String,           //作者名称
    val userIcon: String,           //作者头像
    val name: String,               //作品名称
    val time: String,               //作品发布时间
    val description: String,        //作品描述
    val files: List<RemoteFile>,    //文件
    val pictures: List<Picture>,    //图片
    val comments: List<Comment>,    //评论

    val timestamp: Long = 0         //作品发布时间戳
)

//文件 可能是压缩包、视频等
data class RemoteFile(
    val name: String,               //文件名称
    val url: String                 //下载链接
)

//图片
data class Picture(
    val thumbnail: String,          //缩略图
    val fullImage: String           //大图
)

//评论
data class Comment(
    val id: String,                 //评论id
    val user: String,               //评论发布者名称
    val message: String,            //评论内容
    val time: String,               //发布时间

    val timestamp: Long = 0         //发布时间戳
)