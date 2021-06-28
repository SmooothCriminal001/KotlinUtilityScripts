import java.awt.datatransfer.Clipboard
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter

removeFilesAndGetValidClasses()

fun removeFilesAndGetValidClasses(){
    /*val removeClasses = listOf<String>(
        "AC_EnrollmentController","AC_OrderReview","AC_OrderReviewTest","CC_Bulk_UploadTest","CC_CartItemTest",
        "CampaignMemberFollowUpTestData","CC_StockAvailCheckTest","CalculateBusinessHoursAgesTest","CampaignMemberFollowUpTest",
        "Cex_Invoice_Trigger_Handler","Cex_Invoice_Trigger_Handler","Cex_Invoice_Trigger_Handler","Cex_ShipmentTriggerHandler",
        "Cex_Invoice_Trigger_Handler","Cex_ShipmentTriggerHandler"
    )*/

    val removeClasses = listOf<String>(
        "RetailerStageUpdateBatch", "cex_getShipmentRecommendations", "RetailerStageUpdateBatch_Test", "cex_MyOfferController", "cex_MyOfferController_Test", "cex_ShipmentTriggerActionsTest"
    )

    var filesFinal = "\n"

    File("G:\\Hari\\Office\\ANT Tools\\salesforce_ant_51.0\\sample\\retrieveUnpackaged\\classes").walk()
        .forEach {
            val thisFileName = it.name.substringBefore(".cls");

            if(removeClasses.contains(thisFileName)){
                it.delete()
            }
            else{
                if(it.name.endsWith(".cls-meta.xml")){
                    filesFinal += "\t\t<members>${it.name.substringBefore(".cls-meta.xml")}</members>" + "\n"
                }
            }
        }

    filesFinal += "\t\t"

    val writePath = "G:\\Hari\\Office\\ANT Tools\\salesforce_ant_51.0\\sample\\retrieveUnpackaged\\package.xml"
    val allWriteTextBuffered: BufferedReader = File(writePath).inputStream().bufferedReader()
    val allWriteText = allWriteTextBuffered.use(BufferedReader::readText)

    var startIndex = allWriteText.indexOf("<types>");

    if(startIndex != -1) {
        startIndex = startIndex + 7;
        val endIndex = allWriteText.indexOf ("<name>");

        //val apiPattern = Regex("<types>.+<name>ApexClass</name>")

        //val modifiedContent = apiPattern.replace(allWriteText, filesFinal);

        val modifiedContent = allWriteText.replaceRange(startIndex, endIndex, filesFinal)

        println(modifiedContent)

        //val fileWriter = FileWriter(writePath)
        //fileWriter.write(modifiedContent)
        File(writePath).writeText(modifiedContent)
    }
}