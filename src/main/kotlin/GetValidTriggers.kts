import java.io.BufferedReader
import java.io.File

getValidTriggersMethod()

fun getValidTriggersMethod(){
    File("G:\\Hari\\Office\\ANT Tools\\salesforce_ant_51.0\\sample\\retrieveUnpackaged\\triggers").walk()
        .filter { it.name.endsWith(".trigger-meta.xml")}
        .forEach {

            if(getAPIVersion(it.absolutePath) < 51){
                println("<members>${it.name.substringBefore(".trigger-meta.xml")}</members>")
            }
        }
}

fun getAPIVersion(filePath: String): Double{
    val allText = File(filePath).bufferedReader().use(BufferedReader::readText)
    val apiPattern = Regex("<apiVersion>.+</apiVersion>")

    val version = apiPattern.find(allText, 0)?.value?.substringAfter("<apiVersion>")?.substringBefore("</apiVersion>");

    return version?.toDouble() ?: 0.0
}