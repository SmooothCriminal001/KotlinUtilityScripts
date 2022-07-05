import com.beust.klaxon.*
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import javax.print.Doc
import javax.swing.text.html.HTML

fun getContent(url:String): String{
    val url = URL(url)
    val urlConnection = url.openConnection() as HttpURLConnection

    try {
        val text = urlConnection.inputStream.bufferedReader().readText()
        //println(text)
        return text
    } finally {
        urlConnection.disconnect()
    }
}

fun pickContent(html: String): String{
    val rawContent:String = html.substringAfter("window.__INITIAL_STATE__").substringAfter("\"").substringBefore("\"")
    println(rawContent)
    val prunedContent = URLDecoder.decode(rawContent)
    println(prunedContent)

    return prunedContent
}

fun getChannelDetails(prunedString: String) : ChannelOverview{
    val result:Casted = Klaxon().parse<Casted>(prunedString) as Casted

    return result.ch.overview
}

fun getJsoupDoc(url: String):Document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").get()

fun distillDocJson(url: String): String{
    val document = getJsoupDoc(url)

    val jsonContent = document.select("meta[property=og:description]").first().attr("content")
    return jsonContent
}

data class Casted(val ch: Channel)

data class Channel(val overview: ChannelOverview)

data class ChannelOverview(val cid: Integer, val eids: ArrayList<String>)

data class DocChannel(val channelId: String, val genres: ArrayList<String>, val listened: ArrayList<String>)

//val jsonString: String = pickContent(getContent("https://castbox.fm/channel/The-Knowledge-Project-with-Shane-Parrish-id1364693?country=us"))
//println(getChannelDetails(jsonString))

val docURL = "https://docs.google.com/document/d/1TlnnVqOVk8dkg0sIA-SriVC1BKYggmUDZJPCV4_tSjE/edit"
val docJson = distillDocJson(docURL)

val docResults = Klaxon().parseArray<DocChannel>(docJson) as ArrayList<DocChannel>
println(docResults)
