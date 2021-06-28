import java.net.Inet4Address
import java.net.NetworkInterface

getAddress()

fun getIpv4HostAddress(): String {
    NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
        networkInterface.inetAddresses?.toList()?.find {
            !it.isLoopbackAddress && it is Inet4Address
        }?.let { return it.hostAddress }
    }
    return ""
}

fun getAddress() {
    NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
        networkInterface.inetAddresses?.toList()?.filter { it is Inet4Address }?.forEach {
            println("${it.hostAddress} : ${it is Inet4Address}")
        }
    }
}