package remoteOperations

import java.io.BufferedReader
import java.io.File

class RemoteUtility {
    companion object{
        fun readFileContent(pathUrl: String) = File(pathUrl).bufferedReader().use(BufferedReader::readText)
    }
}