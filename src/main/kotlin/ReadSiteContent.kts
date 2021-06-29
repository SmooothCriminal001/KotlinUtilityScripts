import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.File
import java.net.HttpURLConnection
import java.net.URL


fun showContent(url:String){
    val url = URL(url)
    val urlConnection = url.openConnection() as HttpURLConnection

    try {
        val text = urlConnection.inputStream.bufferedReader().readText()
        println(text)
    } finally {
        urlConnection.disconnect()
    }
}

fun parseContent(url: String, author: String){

    var currentUrl: String? = url
    var overallString: String? = ""

    while(currentUrl != null && currentUrl.isNotBlank()){
        val document: Document = Jsoup.connect(currentUrl).get()
        val postContent: Elements = document.select("div[class=post]")

        var currentString:String? = ""

        postContent.filter {
            it.select("div.post_author > div.author_information > strong > span > a").text() == author
        }.forEach {
            currentString = it.select("div.post_content > div.post_body")?.html()

            if(currentString != null && (!currentString!!.contains("blockquote"))){
                overallString += "<br/><br/>" + currentString
            }
        }

        val nextPage = document.select("div[class=pagination] > a[class=pagination_next]")?.attr("href")
        println("nextPage : $nextPage")

        if(nextPage != null && nextPage.isNotBlank()){
            currentUrl = "https://xossipy.com/$nextPage"
            Thread.sleep(2000)
        }
        else{
            currentUrl = null
        }
        println("currentUrl : $currentUrl")

    }

    File("").printWriter().use { out ->
        out.println(overallString)
    }
}

fun parseContentPlainAuthor(url: String, author: String){

    var currentUrl: String? = url
    var overallString: String? = ""

    while(currentUrl != null && currentUrl!!.isNotBlank()){
        val document: Document = Jsoup.connect(currentUrl).get()
        val postContent: Elements = document.select("div[class=post]")

        var currentString:String? = ""

        postContent.filter {
            it.select("div.post_author > div.author_information > strong > span").text() == author
        }.forEach {
            currentString = it.select("div.post_content > div.post_body")?.html()

            if(currentString != null && (!currentString!!.contains("blockquote"))){
                overallString += "<br/><br/>" + currentString
            }
        }

        val nextPage = document.select("div[class=pagination] > a[class=pagination_next]")?.attr("href")
        println("nextPage : $nextPage")

        if(nextPage != null && nextPage.isNotBlank()){
            currentUrl = "https://xossipy.com/$nextPage"
            Thread.sleep(2000)
        }
        else{
            currentUrl = null
        }
        println("currentUrl : $currentUrl")

    }

    File("").printWriter().use { out ->
        out.println(overallString)
    }
}

fun parseContentPosts(url: String, incomingMap: MutableMap<String, String>? = null, maxTimes: Int = 1){

    var count:Int = maxTimes
    var currentLink:String? = url

    var overallString:String? = ""
    val baseFolder: String = ""
    var currentPath: String = ""

    var entriesMap = incomingMap

    while(currentLink != null && count > 0){

        println(currentLink)
        val document: Document = Jsoup.connect(currentLink).get()

        var postTitle = document.select("h3[class=post-title entry-title]")?.first()?.html()?.replace("[/\\:*?\"<>|]".toRegex(), "")
        var postContent = document.select("div[class^=post-body entry-content]")?.first()?.html()

        if(postTitle == null){
            postTitle = "No Title"
        }

        if(postContent == null){
            println("skipped ${currentLink}")
            continue
        }

        println(postTitle)
        println(count)
        //currentLink = document.select("a[class=blog-pager-older-link]").first().attr("href")
        currentLink = document.select("a[class=blog-pager-newer-link]")?.first()?.attr("href")

        println("next : $currentLink")
        postContent = "<br/><br/><center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"
        //overallString = overallString + "<center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"

        //println(entriesMap)
        currentPath = baseFolder + postTitle + ".html"

        if(entriesMap == null){
            createAndWriteFile(currentPath, postContent)
            entriesMap = mutableMapOf(postTitle to currentPath)
        }
        else{
            var matchedEntries= entriesMap.keys?.filter{ areRelated(it, postTitle!!) < 5}
            var matchedKey: String? = null

            if(!matchedEntries.isEmpty()){
                matchedKey = matchedEntries.first()
            }

            if(matchedKey != null){
                appendToFile(entriesMap.get(matchedKey) as String, postContent)
            }
            else{
                createAndWriteFile(currentPath, postContent)
                entriesMap.put(postTitle,currentPath)
            }
        }
        count--
    }

    /*
    var countNow:Int = 100
    if (entriesMap != null) {
        for((key, value) in entriesMap){
            File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\To\\${++countNow}.html").printWriter().use { out ->
                out.println(value)
            }
        }
    }*/
}

fun parseContentBlog(url: String, incomingMap: MutableMap<String, String>? = null){

    var currentLink:String? = url

    var overallString:String? = ""
    val baseFolder: String = ""
    var currentPath: String = ""

    var entriesMap = incomingMap

    while(currentLink != null){

        println(currentLink)
        val document: Document = Jsoup.connect(currentLink).get()

        var postTitle = document.select("h3[class=post-title entry-title]")?.first()?.html()?.replace("[/\\:*?\"<>|]".toRegex(), "")
        var postContent = document.select("div[class^=post-body entry-content]")?.first()?.html()

        if(postTitle == null){
            postTitle = "No Title"
        }

        if(postContent == null){
            println("skipped ${currentLink}")
            continue
        }

        println(postTitle)
        //currentLink = document.select("a[class=blog-pager-older-link]").first().attr("href")
        currentLink = document.select("a[class=blog-pager-newer-link]")?.first()?.attr("href")

        println("next : $currentLink")
        postContent = "<br/><br/><center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"
        //overallString = overallString + "<center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"

        //println(entriesMap)
        currentPath = baseFolder + postTitle + ".html"

        if(entriesMap == null){
            createAndWriteFile(currentPath, postContent!!)
            entriesMap = mutableMapOf(postTitle as String to currentPath)
        }
        else{
            var matchedEntries= entriesMap!!.keys?.filter{ areRelated(it, postTitle!!) < 5}
            var matchedKey: String? = null

            if(!matchedEntries.isEmpty()){
                matchedKey = matchedEntries.first()
            }

            if(matchedKey != null){
                appendToFile(entriesMap!!.get(matchedKey) as String, postContent!!)
            }
            else{
                createAndWriteFile(currentPath, postContent!!)
                entriesMap!!.put(postTitle!!,currentPath)
            }
        }
    }

    /*
    var countNow:Int = 100
    if (entriesMap != null) {
        for((key, value) in entriesMap){
            File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\To\\${++countNow}.html").printWriter().use { out ->
                out.println(value)
            }
        }
    }*/
}

fun areRelated(firstString: String, secondString: String) = StringUtils.getLevenshteinDistance(firstString, secondString)

fun appendToFile(path: String, content: String) = File(path).appendText(content)

fun createAndWriteFile(path: String, content:String) = File(path).printWriter().use{ out -> out.println(content)}

fun parseContent2(url: String, incomingMap: MutableMap<String, String>? = null){
    var currentLink:String? = url
    var overallString:String? = ""
    val baseFolder: String = ""
    var currentPath: String = ""

    var entriesMap = incomingMap

    while(currentLink != null){
        println(currentLink)
        val document: Document = Jsoup.connect(currentLink).get()

        var postTitle = document.select("h1[class=post-title]")?.first()?.html()?.replace("[/\\:*?\"<>|]".toRegex(), "")
        var postContent = document.select("section[class=story-content]")?.first()?.html()

        if(postTitle == null){
            postTitle = "No Title"
        }

        if(postContent == null){
            println("skipped ${currentLink}")
            continue
        }

        println(postTitle)
        //currentLink = document.select("a[class=blog-pager-older-link]").first().attr("href")

        val currentLinkContainer = document.select("i[class=material-icons]")
            .filter { it.html() == "keyboard_arrow_right" }

        if(!currentLinkContainer.isEmpty()){
            currentLink = currentLinkContainer.first()?.nextElementSibling()?.attr("href")
        }
        else{
            currentLink = null
        }

        println("next : $currentLink")
        postContent = "<br/><br/><center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"
        //overallString = overallString + "<center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"

        //println(entriesMap)
        currentPath = baseFolder + postTitle + ".html"

        if(entriesMap == null){
            createAndWriteFile(currentPath, postContent!!)
            entriesMap = mutableMapOf(postTitle as String to currentPath)
        }
        else{
            var matchedEntries= entriesMap!!.keys?.filter{ areRelated(it, postTitle!!) < 5}
            var matchedKey: String? = null

            if(!matchedEntries.isEmpty()){
                matchedKey = matchedEntries.first()
            }

            if(matchedKey != null){
                appendToFile(entriesMap!!.get(matchedKey) as String, postContent!!)
            }
            else{
                createAndWriteFile(currentPath, postContent!!)
                entriesMap!!.put(postTitle!!,currentPath)
            }
        }
    }
}

fun parseContent2PreList(urlList:List<String>, incomingMap: MutableMap<String, String>? = null)
{
    var overallString:String? = ""
    val baseFolder: String = ""
    var currentPath: String = ""

    var entriesMap = incomingMap

    for(eachUrl in urlList){
        println(eachUrl)
        val document: Document = Jsoup.connect(eachUrl).get()

        var postTitle = document.select("h1[class=post-title]")?.first()?.html()?.replace("[/\\:*?\"<>|]".toRegex(), "")
        var postContent = document.select("section[class=story-content]")?.first()?.html()

        if(postTitle == null){
            postTitle = "No Title"
        }

        if(postContent == null){
            println("skipped ${eachUrl}")
            continue
        }
        println(postTitle)

        postContent = "<br/><br/><center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"

        currentPath = baseFolder + postTitle + ".html"

        if(entriesMap == null){
            createAndWriteFile(currentPath, postContent!!)
            entriesMap = mutableMapOf(postTitle as String to currentPath)
        }
        else{
            var matchedEntries= entriesMap!!.keys?.filter{ areRelated(it, postTitle!!) < 5}
            var matchedKey: String? = null

            if(!matchedEntries.isEmpty()){
                matchedKey = matchedEntries.first()
            }

            if(matchedKey != null){
                appendToFile(entriesMap!!.get(matchedKey) as String, postContent!!)
            }
            else{
                createAndWriteFile(currentPath, postContent!!)
                entriesMap!!.put(postTitle!!,currentPath)
            }
        }
    }
}