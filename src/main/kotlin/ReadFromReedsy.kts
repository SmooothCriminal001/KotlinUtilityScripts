import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.mutable.Mutable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

showContent("https://blog.reedsy.com/short-story/a82idv/?utm_source=mailparrot&utm_campaign=prompts_2021_year_in_review")
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

