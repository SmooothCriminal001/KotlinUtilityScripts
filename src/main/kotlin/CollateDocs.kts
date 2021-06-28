import java.io.BufferedReader
import java.io.File
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import java.io.FileInputStream


var newFileName:String? = null
var currentNum:Int = 0
var totalString:String = ""

File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\From").walk()
    .filter { it.isDirectory }
    .forEach {
        if(it.name != "From"){
            println(it.name)
            totalString = ""
            currentNum = File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\From\\${it.name}").listFiles().size

            for(i in currentNum downTo 1){
                val inputStream: FileInputStream = FileInputStream(File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\From\\${it.name}\\${i}.docx"))
                val document = XWPFDocument(inputStream)
                val paragraphs = document.paragraphs
                totalString += paragraphs.joinToString(separator = "") {
                    it.text
                }
                inputStream.close()
            }

            File("C:\\Users\\ HARRY\\Desktop\\TestingFiles\\To\\${it.name}.html").printWriter().use { out ->
                out.println(totalString)
            }
        }
    }
