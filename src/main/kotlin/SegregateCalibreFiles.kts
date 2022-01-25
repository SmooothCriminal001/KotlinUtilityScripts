import java.io.File
import java.io.*
import java.nio.file.Files
import java.nio.file.*

var fileName = 0

val excludeFiles : Set<String> = setOf(
)

File("G:\\Hari\\Softwares\\Calibre Portable\\Calibre Library").walk()
    .forEach {
        println(it.name)
        if(it.name.endsWith(".epub")){
            File(it.absolutePath).renameTo(File("G:\\Hari\\Softwares\\Calibre Portable\\Epubs\\${it.name}"))
        }
        else if(it.name.endsWith(".txt")){
            File(it.absolutePath).renameTo(File("G:\\Hari\\Softwares\\Calibre Portable\\Txts\\${it.name}"))
        }
        else if(it.name.endsWith(".htmlz")){
            File(it.absolutePath).renameTo(File("G:\\Hari\\Softwares\\Calibre Portable\\Htmls\\${it.name}"))
        }
    }