# kemono-api
Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url = uri("https://jitpack.io") }
	}
}
```
Step 2. Add the dependency
```
dependencies {
    implementation("com.github.WhichWho:kemono-api:1.17")
}
```
## example
```
    //登录 & 注册
//    val account = AccountNetwork.login("username", "password")
//    val account = AccountNetwork.register("username", "password")
    val account = Account(
        "username", "password",
        "session=xxxxxxxxxx",
        1645863026000
    )
    AccountNetwork.setAccount(account)

    //所有收藏的作者
    val favoriteArtists = AccountNetwork.favoriteArtists()
    println(favoriteArtists)

    //通过收藏的作者获取作者摘要（本地计算）
    val artistSummary = favoriteArtists.first().toSimpleArtist()
    println(artistSummary)

    //通过收藏的作者获取作者详情（网络请求）
    val artistDetail = favoriteArtists.first().toArtist()
    println(artistDetail)

    //所有收藏的作品
    val favoritePosts = AccountNetwork.favoritePosts()
    println(favoritePosts)

    //通过收藏的作品获取作品摘要（本地计算）
    val postSummary = favoritePosts.first().toSimplePost()
    println(postSummary)

    //通过收藏的作品获取作品详情（网络请求）
    val postDetail = favoritePosts.first().toPictureGroup()
    println(postDetail)


    //全部作者列表
    val artists = Network.allArtist()
//    val artists = File("C:\\Users\\hp\\Desktop\\creators.json").readText().toAllArtist()
    println("artists.size: ${artists.size}")


    //作者摘要信息
    val simpleArtist = artists.first()
    println(simpleArtist)


    //作者详细信息
//    val url = ArtistFactory.getArtistUrlById(ArtistService.fanbox.name, "3115085", 25)
//    val artistPage = Network.doRequest(url)?.toArtist()
//    val artistPage = File("C:\\Users\\hp\\Desktop\\3115085.html").readText().toArtist()
    val artistPage = simpleArtist.toArtist(offset = 0)
    println(artistPage)


    //作品详细信息
//    val url = PictureFactory.getPictureUrl(ArtistService.fanbox.name, "3115085", "1784140")
//    val group = Network.doRequest(url)?.toPictureGroup()
//    val group = File("C:\\Users\\hp\\Desktop\\1784140.html").readText().toPictureGroup()
    val group = artistPage.simplePosts.first().toPost()
    println(group)
    
    //获取所有作品，支持搜索，分页
    println(Network.allPosts(1500))
    
```
