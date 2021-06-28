package remoteOperations

import com.beust.klaxon.Klaxon
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.File

data class TokenInfo(
    val access_token: String, val refresh_token: String
)
{
    companion object{
        var tokenJson: TokenInfo? = null

        fun getTokens(): TokenInfo?{
            if(tokenJson == null){
                val tokenText = RemoteUtility.readFileContent("G:\\Hari\\Softwares\\Remote Particulars\\access info\\tokenJson")
                tokenJson =  Klaxon().parse<TokenInfo>(tokenText) as TokenInfo
            }
            return tokenJson
        }

    }
}