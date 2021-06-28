
/*
val tortoiseOnHareStart: Double? = readLine()?.toDouble()
val hareOnTie:Double? = readLine()?.toDouble()
*/

val tortoiseOnHareStart: Double? = 7.0
val hareOnTie:Double? = 8.0

if(tortoiseOnHareStart != null && hareOnTie != null) {
    val tortoiseOnHareStartFraction = 1 / tortoiseOnHareStart
    val hareOnTieFraction = 1 / hareOnTie

    println("$tortoiseOnHareStartFraction && $hareOnTieFraction")

    val tortoiseOnTieFraction = (1 - hareOnTieFraction) - tortoiseOnHareStartFraction

    val tortoiseRateToHare = tortoiseOnTieFraction / hareOnTieFraction

    val hareToImprove = tortoiseRateToHare * (hareOnTie - 1)

    println(hareToImprove)
}