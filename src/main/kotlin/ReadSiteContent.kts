import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import org.apache.commons.lang3.StringUtils
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyManagementException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

fun showContent(url:String){
    val url = URL(url)
    val urlConnection = url.openConnection() as HttpURLConnection
    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")

    try {
        val text = urlConnection.inputStream.bufferedReader().readText()
        println(text)
    } finally {
        urlConnection.disconnect()
    }
}

fun parseContent(url: String, author: String, fileName: String, baseUrl: String){

    var currentUrl: String? = url
    var overallString: String? = ""

    var count = 0
    while(currentUrl != null && currentUrl.isNotBlank() && count<=200){
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
            currentUrl = baseUrl + nextPage
            Thread.sleep(500)
        }
        else{
            currentUrl = null
        }
        println("currentUrl : $currentUrl")
        count++
    }

    File("C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\$fileName.html").printWriter().use { out ->
        out.println(overallString)
    }
}

fun parseContentPlainAuthor(url: String, author: String, fileName: String){

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
            currentUrl = nextPage
            Thread.sleep(500)
        }
        else{
            currentUrl = null
        }
        println("currentUrl : $currentUrl")

    }

    File("C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\$fileName.html").printWriter().use { out ->
        out.println(overallString)
    }
}

fun parseContentPosts(url: String, incomingMap: MutableMap<String, String>? = null, maxTimes: Int = 1){

    var count:Int = maxTimes
    var currentLink:String? = url

    var overallString:String? = ""
    val baseFolder: String = "C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\"
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
            var matchedEntries= entriesMap.keys?.filter{ areRelated(it, postTitle!!)}
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
}

fun parseContentBlog(url: String, incomingMap: MutableMap<String, String>? = null, quarterSimilarty: Boolean = true, onlyLevenshtein: Boolean = false){

    var currentLink:String? = url

    val baseFolder: String = "C:\\Users\\ HARRY\\Desktop\\TestFiles\\From\\"
    var currentPath: String = ""

    var finalString = "<table border=\"1\">"

    var entriesMap = incomingMap

    while(currentLink != null){

        val document: Document = Jsoup.connect(currentLink).get()

        var postTitle = document.select("h3[class=post-title entry-title]")?.first()?.html()?.replace("[/\\:*?\"<>|]".toRegex(), "")
        var postContent = document.select("div[class^=post-body entry-content]")?.first()?.html()

        if(postTitle == null){
            postTitle = "No Title"
        }

        finalString += "<tr><td>$postTitle</td><td>$currentLink</td></tr>"

        //currentLink = document.select("a[class=blog-pager-older-link]").first().attr("href")
        currentLink = document.select("a[class=blog-pager-newer-link]")?.first()?.attr("href")

        println("next : $currentLink")


        if(postContent == null){
            println("skipped ${currentLink}")
            continue
        }

        postContent = "<br/><br/><center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"
        //overallString = overallString + "<center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"

        println(entriesMap)
        currentPath = baseFolder + postTitle + ".html"

        if(entriesMap == null){
            createAndWriteFile(currentPath, postContent!!)
            entriesMap = mutableMapOf(postTitle as String to currentPath)
        }
        else{
            var matchedEntries= entriesMap!!.keys?.filter{ areRelated(it, postTitle!!, quarterSimilarty, onlyLevenshtein=onlyLevenshtein)}
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

fun parseContentBlogSpecial(url: String, incomingMap: MutableMap<String, String>? = null, excludePhrase: String){

    var currentLink:String? = url

    var overallString:String? = ""
    val baseFolder: String = "C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\"
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

        println(postTitle)
        //currentLink = document.select("a[class=blog-pager-older-link]").first().attr("href")
        currentLink = document.select("a[class=blog-pager-newer-link]")?.first()?.attr("href")

        if(postContent == null || postContent!!.contains(excludePhrase)){
            println("skipped ${currentLink}")
            continue
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
            var matchedEntries= entriesMap!!.keys?.filter{ areRelated(it, postTitle!!)}
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

fun areRelated(firstString: String, secondString: String, quarterSimilarty: Boolean = true, onlyLevenshtein: Boolean = false): Boolean{

    /*val distance = StringUtils.getLevenshteinDistance(firstString, secondString)
    //println("distance $distance")
    //println("length ${firstString.length}")
    val threshold = (firstString.length * 0.75).toInt()
    //println("threshold $threshold")

    return distance < threshold*/

    if(onlyLevenshtein){
        return (StringUtils.getLevenshteinDistance(firstString, secondString) < 5)
    }
    else{
        val similarityExtent = if(quarterSimilarty) 0.25 else 0.5

        var referLenth = (firstString.length * similarityExtent).toInt()
        //var referLenth = (firstString.length * 0.5).toInt()
        var firstCut:String = ""
        var secondCut: String = ""

        if(secondString.trim().length >= referLenth){
            firstCut = firstString.trim().substring(0, referLenth)
            secondCut = secondString.trim().substring(0, referLenth)

            if(firstCut == secondCut){
                return true
            }
            else{
                firstCut = firstString.trim().substring(firstString.length - referLenth)
                secondCut = secondString.trim().substring(secondString.length - referLenth)

                if(firstCut == secondCut){
                    return true
                }
                else{
                    return (StringUtils.getLevenshteinDistance(firstString, secondString) < 7)
                }
            }
        }
        else{
            return false
        }
    }

}

fun appendToFile(path: String, content: String) = File(path).appendText(content)

fun createAndWriteFile(path: String, content:String) = File(path).printWriter().use{ out -> out.println(content)}

fun parseContent2(url: String, incomingMap: MutableMap<String, String>? = null){
    var currentLink:String? = url
    var overallString:String? = ""
    val baseFolder: String = "C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\"
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
            var matchedEntries= entriesMap!!.keys?.filter{ areRelated(it, postTitle!!)}
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
            var matchedEntries= entriesMap!!.keys?.filter{ areRelated(it, postTitle!!)}
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

data class Content(
    val title: String,
    val url: String,
    val repeats: Int,
    val overToNew: Boolean
)

fun parseContentBlog(incomingItems: List<Content>){

    val baseFolder: String = "C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\"
    var currentLink: String? = ""
    var currentPath: String? = ""
    var count: Int = 0

    for(content in incomingItems){
        currentLink = content.url
        currentPath = baseFolder + content.title + ".html"
        createAndWriteFile(currentPath!!, "")
        count = 1

        while(currentLink != null && count <= content.repeats){
            println(currentLink)

            val document: Document = Jsoup.connect(currentLink).get()

            val postTitle = content.title + if(content.repeats == 1) "" else " $count"
            count++

            println(postTitle)

            var postContent = document.select("div[class^=post-body entry-content]")?.first()?.html()
            if(postContent == null){
                println("skipped ${currentLink}")
                continue
            }

            //val linkDirection = if(content.overToNew) "a[class=blog-pager-newer-link]" else "a[class=blog-pager-older-link]"
            currentLink = document.select("a[class=blog-pager-newer-link]").first().attr("href")

            println("next : $currentLink")
            postContent = "<br/><br/><center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"

            appendToFile(currentPath!!, postContent!!)
            Thread.sleep(2000)
        }

    }
}

fun parseContent3(incomingItems: List<Content>){

    val baseFolder: String = ""
    var currentLink: String? = ""
    var currentPath: String? = ""
    var count: Int = 0

    for(content in incomingItems){
        currentLink = content.url
        currentPath = baseFolder + content.title + ".html"
        createAndWriteFile(currentPath!!, "")
        count = 1

        while(currentLink != null && count <= content.repeats){
            println(currentLink)

            val document: Document = Jsoup.connect(currentLink).get()

            val postTitle = content.title + if(content.repeats == 1) "" else " $count"
            count++

            println(postTitle)

            var postContent = document.select("div[class^=td-post-content]")?.first()?.html()
            if(postContent == null){
                println("skipped ${currentLink}")
                continue
            }

            //val linkDirection = if(content.overToNew) "a[class=blog-pager-newer-link]" else "a[class=blog-pager-older-link]"
            currentLink = document.select("div[class=td-block-span6 td-post-next-post] > div > a").first().attr("href")

            println("next : $currentLink")
            postContent = "<br/><br/><center><h2 class=\"chapter\">$postTitle</h2></center>$postContent"

            appendToFile(currentPath!!, postContent!!)
            Thread.sleep(2000)
        }

    }
}

fun parsePageLists(url: String, repeatThread : String? = null, baseUrl: String = "") {

    var afterThread = repeatThread
    var nextPageUrl: String? = url
    val allEntries: MutableMap<String, String> = mutableMapOf()

    while (nextPageUrl != null) {
        val document: Document = Jsoup.connect(nextPageUrl).get()

        var includeTr = false
        val consideredElements: MutableList<Element> = mutableListOf()

        document.select("tr").forEach {
            if (afterThread == null && it.select("td").first().text() == "Normal Threads" && !includeTr) {
                includeTr = true
            }
            else if(afterThread != null && !includeTr){
                includeTr = it.select("a").any { it.attr("href").contains(afterThread!!) }
            }
            else if (includeTr && it.attr("class") == "inline_row") {
                consideredElements.add(it)
            }
        }

        consideredElements.forEach {
            val allAs = it.select("a[href^=thread-]").filter {
                !it.attr("href").contains("-newpost") && !it.attr("href").contains("-lastpost")
            }

            val threadUrl = allAs.first().attr("href")
            val title = allAs.first().text().replace("[/\\:*?\"<>|]".toRegex(), "")
            val threadName = threadUrl.substringBefore(".")
            val url = baseUrl + threadUrl



            val maxPageElement = allAs.filter {
                it.attr("href").startsWith("$threadName-page")
            }

            if(!maxPageElement.isEmpty()){
                val maxPages = maxPageElement.last().text()?.trim()?.toInt()

                print("title $title - $maxPages")

                val consent: String? = readLine()

                if(consent!=null && consent.isNotBlank()){
                    return@forEach
                }

                if (maxPages != null && maxPages >= 2) {
                    val author = it.select("div[class=author smalltext]").text()

                    //completeString += "<tr><td>$title</td><td>$author</td><td>$url</td><td>$maxPages</td></tr>"
                    parseContentAllAuthor(url, author, title, baseUrl)
                }

            }
        }

        val nextPage = document.select("a[class=pagination_next]")?.attr("href");

        print("$nextPage : ")
        if (nextPage != null && nextPage.isNotBlank()) {
            afterThread = null
            nextPageUrl = (baseUrl + nextPage).trim()
            println("NEXT LIST PAGE $nextPage")
        } else {
            println("whole thing ends")
            nextPageUrl = null
        }
    }
}

fun parseContentAllAuthor(url: String, author: String, title: String, baseUrl: String){

    var currentUrl: String? = url
    var overallString: String? = ""

    while(currentUrl != null && currentUrl!!.isNotBlank()){
        val document: Document = Jsoup.connect(currentUrl).get()
        val postContent: Elements = document.select("div[class=post]")

        var currentString:String? = ""

        postContent.filter {
            it.select("div.post_author > div.author_information > strong > span > a")?.text() == author ||
            it.select("div.post_author > div.author_information > strong > span")?.text() == author
        }.forEach {
            currentString = it.select("div.post_content > div.post_body")?.html()

            if(currentString != null && (!currentString!!.contains("blockquote"))){
                overallString += "<br/><br/>" + currentString
            }
        }

        val nextPage = document.select("div[class=pagination] > a[class=pagination_next]")?.attr("href")
        println("nextPage : $nextPage")

        if(nextPage != null && nextPage.isNotBlank()){
            currentUrl = "$baseUrl$nextPage"
        }
        else{
            currentUrl = null
        }

    }

    val baseFolder: String = "C:\\Users\\ HARRY\\Desktop\\TestFiles\\From\\"
    createAndWriteFile(baseFolder + title + ".html", overallString as String)
}

fun parseSeries(seriesUrl: String, maxNum: Int? = null){
    var currentUrl:String? = seriesUrl
    val linksList: MutableList<String> = mutableListOf()
    var title = ""

    while(!currentUrl.isNullOrEmpty()){
        var document: Document = Jsoup.connect(currentUrl).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").get()

        document.select("h2[class=entry-title]").forEach {
            linksList.add(it.select("a").attr("href"))
        }

        title = document.select("h1[class=page-title] > span")?.text() ?: "NoTitle"

        currentUrl = document.select("a[class=next page-numbers]")?.attr("href")
    }

    if(maxNum != null && linksList.size < maxNum) {
        println("skipped $title")
        return
    }

    title = title.replace("[/\\:*?\"<>|]".toRegex(), "")

    var overallString = ""
    linksList.reversed().forEach {
        println(it)
        val entry: Document = Jsoup.connect(it).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").get()

        entry.select("div[class=entry-content] > section").remove()
        overallString += "<br/><br/>" + entry.select("div[class=entry-content]").html()
    }

    File("C:\\Users\\ HARRY\\Desktop\\TestFiles\\From\\$title.html").printWriter().use { out ->
        out.println(overallString)
    }
}

fun parseSeriesMain(url: String){
    val document: Document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").get()
    var enableProcess = false

    document.select("h2[class=entry-title] > a").forEach {
        val currentHref = it.attr("href")

        if(enableProcess){
            parseSeries(it.attr("href"), maxNum = 5)
        }
        else{
            println("skipped outer$enableProcess : $currentHref")
        }

        if(currentHref == ""){
            enableProcess = true
        }
    }
}

fun parseSeriesEng(seriesUrl: String){
    var currentUrl:String? = seriesUrl
    val linksList: MutableList<String> = mutableListOf()
    var title = ""

    while(!currentUrl.isNullOrEmpty()){
        var document: Document = getConnection(currentUrl!!)

        document.select("h2[class=post-title]").forEach {
            linksList.add(it.select("a").attr("href"))
        }

        title = document.select("div[class=archive-title] > h1")?.text() ?: "NoTitle"

        currentUrl = document.select("a[class=next page-numbers]")?.attr("href")
    }

    /*if(maxNum != null && linksList.size < maxNum) {
        println("skipped $title")
        return
    }*/

    title = title.replace("[/\\:*?\"<>|]".toRegex(), "")

    var overallString = ""
    linksList.reversed().forEach {
        println(it)
        val entry: Document = getConnection(it)

        overallString += "<br/><br/><h1>${entry.select("h1[class=post-title]").text()}</h1><br/>" + entry.select("section[class=story-content]").html()
    }

    File("C:\\Users\\ HARRY\\Desktop\\TestFiles\\From\\$title.html").printWriter().use { out ->
        out.println(overallString)
    }
}

fun getConnection(url: String): Document{
    lateinit var document: Document

    try{
        document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").get()
    }
    catch (e: HttpStatusException){
        if(e.statusCode == 525){
            println("sleeping for $url")
            Thread.sleep(60000)
            return getConnection(url)
        }
    }

    return document
}

fun socketFactory(): SSLSocketFactory {
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    })

    try {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        return sslContext.socketFactory
    } catch (e: Exception) {
        when (e) {
            is RuntimeException, is KeyManagementException -> {
                throw RuntimeException("Failed to create a SSL socket factory", e)
            }
            else -> throw e
        }
    }
}

data class PostContent(
    val url: String,
    val times: Int
)

fun parseContentBlog2(incomingItems: List<PostContent>){
    for(content in incomingItems){
        parseContentPosts(content.url, maxTimes = content.times)
    }
}

fun parseContentNewSinglePg(url: String){

    val baseFolder: String = "C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\"
    val document: Document = Jsoup.connect(url).get()

    var postTitle = document.select("h1[class=entry-title]")?.text()
    var postContent = document.select("div[class=entry-content entry-content-single]")?.first()?.html()

    if(postTitle == null){
        postTitle = "No Title"
    }

    File("C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\$postTitle.html").printWriter().use { out ->
        out.println(postContent)
    }
}

fun parseFiles(){

    var completeFile: String = ""
    File("G:\\Hari\\Programming\\TempFile\\").walk()
        .filter { it.extension == "html"}
        .filter { pickFile(it.name.removeSuffix(".html"))}
        .forEach {

            it.delete()
            /*
                println(it.name)
            completeFile += File(it.absolutePath).bufferedReader().use(BufferedReader::readText)

             */
        }

    /*File("C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\compilation.html").printWriter().use { out ->
        out.println(completeFile)
    }*/
}

fun pickFile(fileName: String): Boolean{
    val reversedName = fileName.reversed()
    val maxLength = if(reversedName.length < 5) reversedName.length else 3

    for(i in 0 until maxLength){
        if(reversedName[i].isDigit()){
            return false;
        }
    }

    return true;
}

fun parseContentW(url: String, baseUrl: String){
    val document: Document = Jsoup.connect(url).get()

    val urlsList = mutableListOf<String>()

    document.select("ul[class*=table-of-contents] > li").forEach {
        urlsList.add(baseUrl + (it.select("a[class=on-navigate]")?.attr("href") ?: throw IllegalArgumentException("Not valid url")))
    }

    val title = document.select("span[class=info] > h2[class*=title]")?.text() ?: "New Title"
    var content = ""

    urlsList.forEachIndexed { indx, sect ->
        println(sect)
        val eachSect: Document = Jsoup.connect(sect).get()

        val sectTitle = eachSect.select("header[class=panel panel-reading text-center] > h1[class=h2]")?.text() ?: indx
        content += "<br/><br/><center><h2 class=\"chapter\">$sectTitle</h2></center>"

        eachSect.select("div[class$=panel panel-reading]").forEach {
            content += it.html()
        }

        return
    }

    println(content)
    File("C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\$title.html").printWriter().use { out ->
        out.println(content)
    }
}

fun testPDFExtract(){
    val reader = PdfReader("G:\\Hari\\Books\\Testing.pdf")
    var resultText = ""

    for(i in 1..reader.numberOfPages){
        resultText += PdfTextExtractor.getTextFromPage(reader, i, ) + "\n"
    }

    print("result Text : $resultText")

    File("C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\Testing.txt").printWriter().use { out ->
        out.println(resultText)
    }
}

fun parseContentFa(url: String, author: String, baseUrl: String = ""){

    var currentUrl: String? = url
    var overallString: String? = ""

    var count = 0

    var title: String? = null

    while(currentUrl != null && currentUrl!!.isNotBlank() && count<=200){
        val document: Document = Jsoup.connect(currentUrl).get()

        if(title == null){
            document.select("div[class=p-title ] > h1[class=p-title-value] > *").remove()
            title = document.select("div[class=p-title ] > h1[class=p-title-value]")?.first()?.text()?.replace("[/\\:*?\"<>|]".toRegex(), "") ?: "No title"
        }

        document.select("div[class=message-inner]").forEach {
                val authName = it.select("div[class=message-userDetails] > h4[class=message-name] > a").text()

            if(authName == author){
                overallString += it.select("div[class=bbWrapper]").first().html() + "<br/><br/>"
            }
        }

        val nextPage = document.select("a[class*=pageNav-jump--next]")?.first()?.attr("href")
        println("nextPage : $nextPage")

        if(nextPage != null && nextPage.isNotBlank()){
            currentUrl = baseUrl + nextPage
        }
        else{
            currentUrl = null
        }
        println("currentUrl : $currentUrl")
        count++
    }

    File("C:\\Users\\ HARRY\\Desktop\\TestFiles\\To\\$title.html").printWriter().use { out ->
        out.println(overallString)
    }
}