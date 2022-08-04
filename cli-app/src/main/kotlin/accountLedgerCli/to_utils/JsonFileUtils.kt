package accountLedgerCli.to_utils

import accountLedgerCli.to_models.IsOkModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.io.FileOutputStream

object JsonFileUtils {

    @OptIn(ExperimentalSerializationApi::class)
    @JvmStatic
    inline fun <reified T> readJsonFile(fileName: String, isDevelopmentMode: Boolean = false): IsOkModel<T> {

        val jsonFile = File(fileName)
        if (isDevelopmentMode) {

            println("jsonFile = $jsonFile")
            println("jsonFile exists = ${jsonFile.exists()}")
        }
        if (jsonFile.exists()) {

            jsonFile.inputStream().use { fileInputStream ->

                val result: T = Json.decodeFromStream(fileInputStream)
                if (isDevelopmentMode) {

                    println("data = $result")
                }
                return IsOkModel(isOK = true, data = result)
            }
        }
        return IsOkModel(isOK = false)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @JvmStatic
    inline fun <reified T> writeJsonFile(fileName: String, data: T) {

        FileOutputStream(File(fileName)).use { fileOutputStream ->

            Json.encodeToStream(data, fileOutputStream)
        }
    }
}