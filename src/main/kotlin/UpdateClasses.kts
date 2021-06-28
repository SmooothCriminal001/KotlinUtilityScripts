import java.io.BufferedReader
import java.io.File
import java.io.FileWriter

private val UPDATE_VERSION = "51.0"


val format = "%-50s%-20s"

//val validVersions = mutableListOf<String>()
//val invalidVersions = mutableListOf<String>()

File("G:\\Hari\\Office\\ANT Tools\\salesforce_ant_51.0\\sample\\retrieveUnpackaged\\classes").walk()
    .filter { it.name.endsWith(".cls-meta.xml") || it.name.endsWith(".trigger-meta.xml") }
    .forEach {

        if(getAPIVersion(it.absolutePath) < 51){
            println("<members>${it.name.substringBefore(".cls-meta.xml")}</members>")
        }
        /*val thisVersion = getAPIVersion(it.absolutePath)
        val thisCompName = it.name.substringBefore(".cls-meta.xml")
        if(thisVersion > 50.0){
            validVersions.add(String.format(format, thisCompName, thisVersion))
        }
        else{
            invalidVersions.add(String.format(format, thisCompName, thisVersion))
        }*/
    }

/*
println("Valid components : ${validVersions.size}")
validVersions.forEach { println(it) }

println("\n\n\n")
println("Invalid components : ${invalidVersions.size}")
invalidVersions.forEach { println(it) }
*/

fun readFile(filePath: String){

    val allText = File(filePath).bufferedReader().use(BufferedReader::readText)
    val apiPattern = Regex("<apiVersion>.+</apiVersion>")

    val modifiedXML = apiPattern.replace(allText, "<apiVersion>$UPDATE_VERSION</apiVersion>")

    val writeFile = FileWriter(filePath)
    writeFile.write(modifiedXML)
    writeFile.close()
}

fun getAPIVersion(filePath: String): Double{
    val allText = File(filePath).bufferedReader().use(BufferedReader::readText)
    val apiPattern = Regex("<apiVersion>.+</apiVersion>")

    val version = apiPattern.find(allText, 0)?.value?.substringAfter("<apiVersion>")?.substringBefore("</apiVersion>");

    return version?.toDouble() ?: 0.0
}