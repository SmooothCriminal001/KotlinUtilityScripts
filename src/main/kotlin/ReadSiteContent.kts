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
            currentUrl = "/$nextPage"
            Thread.sleep(2000)
        }
        else{
            currentUrl = null
        }
        println("currentUrl : $currentUrl")

    }

    File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\To\\28.html").printWriter().use { out ->
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

    File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\To\\31.html").printWriter().use { out ->
        out.println(overallString)
    }
}

