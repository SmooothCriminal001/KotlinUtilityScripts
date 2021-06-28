import java.io.File
import java.io.*
import java.nio.file.Files
import java.nio.file.*

var fileName = 0

File("G:\\Hari\\Softwares\\Calibre Portable\\Calibre Library").walk()
    .filter{it.name.endsWith(".epub")}
    .forEach {
        println(it.name)

        File(it.absolutePath).renameTo(File("G:\\Hari\\Softwares\\Calibre Portable\\Epubs\\${it.name}"))
    }
