import java.io.BufferedReader
import java.io.File
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import java.io.FileInputStream


var newFileName:String? = null
File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\From").walk()
    .filter { it.name != "From"}
    .forEach {

        println("original name ${it.name}")
        println("thispath ${it.absolutePath}")

        /*
        val inputStream: FileInputStream = FileInputStream(it.absolutePath)
        val document: HWPFDocument = HWPFDocument(inputStream)
        val extractor: WordExtractor = WordExtractor(document)

        val fileData = extractor.paragraphText.joinToString(separator = ""){it}

        File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\To\\${it.name.removeSuffix(".docx")}.html").printWriter().use { out ->
            out.println(fileData)
        }*/

        val inputStream: FileInputStream = FileInputStream(it.absolutePath)
        val document = XWPFDocument(inputStream)
        val paragraphs = document.paragraphs
        val fileData = paragraphs.joinToString(separator = "") {
            it.text
        }
        inputStream.close()

        File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\To\\${it.name.removeSuffix(".docx")}.html").printWriter().use { out ->
            out.println(fileData)
        }
    }
