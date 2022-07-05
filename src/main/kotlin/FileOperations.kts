import java.io.BufferedReader
import java.io.File
import java.io.FileWriter


fun moveFiles(startPath: String, endPath: String){
    val sortedFiles = File(startPath).walk()
        .filter {
            !it.isDirectory
        }
        .sortedBy {
            it.length()
        }

    val filesList = sortedFiles.toList()
    var count = 0;
    val tempFilePath = "$endPath\\refFile.html";
    createAndWriteFile(tempFilePath, "")
    val maxSize = 5000000
    var collCount = 0
    var currentFilePath = ""
    var currentFileName = ""
    val allFiles = mutableListOf<String>()
    var toc = ""
    val anchor = System.currentTimeMillis().toString()

    while(count < filesList.size){
        val currentFile = filesList[count]
        println(currentFile.name)

        if(currentFile.length() >= maxSize){
            copyFile(startPath, endPath, currentFile.name)
        }
        else{
            var content = readFile(currentFile.absolutePath)
            content = "<h1>${currentFile.name}</h1><section>$content</section>"
            val startEmpty = isFileEmpty(tempFilePath)
            appendToFile(tempFilePath, content)

            if(startEmpty || getFileSize(tempFilePath) > maxSize){

                if(!currentFilePath.isBlank() && currentFileName.startsWith("collection")){
                    var previousContent = readFile(currentFilePath)
                     previousContent = "<p hidden>${allFiles.toString()}</p><div id=\"toc\">$toc</div><br/><br/>$previousContent"
                    emptyFile(currentFilePath)
                    appendToFile(currentFilePath, previousContent)
                    toc = ""
                    allFiles.clear()
                }

                currentFileName = currentFile.name
                currentFilePath = "$endPath\\$currentFileName.html"
                createAndWriteFile(currentFilePath, content)
                emptyFile(tempFilePath)
                appendToFile(tempFilePath, content)
                toc += "<a href=\"#$count\">${currentFile.name}</a><br/>"
                content = "<a id=\"$count\">*</a>" + content
                allFiles.add(currentFile.name)
            }
            else{
                if(!currentFileName.startsWith("collection")){
                    collCount+=1
                    currentFileName = "collection$anchor-$collCount"
                    renameFile(currentFilePath, "$endPath\\$currentFileName.html")
                    currentFilePath = "$endPath\\$currentFileName.html"
                }
                toc += "<a href=\"#$count\">${currentFile.name}</a><br/>"
                allFiles.add(currentFile.name)
                content = "<a id=\"$count\">*</a>" + content
                appendToFile(currentFilePath, content)
            }
        }

        count++;
    }

    if(!currentFilePath.isBlank() && currentFileName.startsWith("collection")){
        var previousContent = readFile(currentFilePath)
        previousContent = "<p hidden>${allFiles.toString()}</p><div id=\"toc\">$toc</div><br/><br/>$previousContent"
        emptyFile(currentFilePath)
        appendToFile(currentFilePath, previousContent)
        toc = ""
        allFiles.clear()
    }

    File(tempFilePath).delete()
}

fun copyFile(sourcePath: String, destPath: String, fileName: String){
    File("$sourcePath\\$fileName").copyTo(File("$destPath\\$fileName"))
}

fun emptyFile(path: String) = FileWriter(path).close()

fun createAndWriteFile(path: String, content:String) = File(path).printWriter().use{ out -> out.println(content)}

fun appendToFile(path: String, content: String) = File(path).appendText(content)

fun readFile(path: String) = File(path).bufferedReader().use(BufferedReader::readText)

fun readFile(file: File) = file.bufferedReader().use(BufferedReader::readText)

fun isFileEmpty(path: String): Boolean = readFile(path).isBlank()

fun getFileSize(path: String) = File(path).length()

fun getFileSize(file: File) =  file.length()

fun renameFile(path: String, currentName: String, rename: String): Boolean{
    val currentPath = "$path\\$currentName"
    val renamePath = "$path\\$rename"

    return File(currentPath).renameTo(File(renamePath))
}

fun renameFile(currentFilePath: String, renamePath: String): Boolean = File(currentFilePath).renameTo(File(renamePath))
