import java.io.BufferedReader
import java.io.File

val readFilePath = "C:\\Users\\ HARRY\\Desktop\\Academy\\UtilityLogs\\OutFile.txt"
val writeFilePath = "C:\\Users\\ HARRY\\Desktop\\Academy\\UtilityLogs\\Result.txt"

val incomingText = File(readFilePath).bufferedReader().use(BufferedReader::readText)

val tabStr = "        "
var tabPrefix = ""
var resultText = ""
val openBracketsList = mutableListOf('(', '[', '{')
val closedBracketsList = mutableListOf(')', ']', '}')
val comma = ','

incomingText.forEach {

    if(it in openBracketsList){
        stepForward(it)
    }
    else if(it == comma){
        addWithNewLine(it)
    }
    else if(it in closedBracketsList){
        stepBack(it)
    }
    else{
        resultText += it
    }
}

File(writeFilePath).printWriter().use{ out -> out.println(resultText)}

fun addTab(){
    tabPrefix += tabStr
}

fun removeTab(){
    tabPrefix.removeSuffix(tabStr)
}

fun addToResult(thisChar: Char){
    resultText += tabPrefix + thisChar
}

fun stepForward(thisChar: Char){
    addTab()
    addWithNewLine(thisChar)
}

fun stepBack(thisChar: Char){
    removeTab()
    addWithNewLine(thisChar)
}

fun addWithNewLine(thisChar: Char){
    resultText += "\n"
    addToResult(thisChar)
}
