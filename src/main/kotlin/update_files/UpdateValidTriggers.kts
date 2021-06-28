package update_files

import java.io.BufferedReader
import java.io.File
import java.io.FileWriter

private val UPDATE_VERSION = "51.0"

File("G:\\Hari\\Office\\ANT Tools\\salesforce_ant_51.0\\sample\\retrieveUnpackaged\\triggers").walk()
    .filter { it.name.endsWith(".trigger-meta.xml")}
    .forEach {
        updateFile(it.absolutePath)
    }

fun updateFile(filePath: String){

    val allText = File(filePath).bufferedReader().use(BufferedReader::readText)
    val apiPattern = Regex("<apiVersion>.+</apiVersion>")

    val modifiedXML = apiPattern.replace(allText, "<apiVersion>$UPDATE_VERSION</apiVersion>")

    val writeFile = FileWriter(filePath)
    writeFile.write(modifiedXML)
    writeFile.close()
}