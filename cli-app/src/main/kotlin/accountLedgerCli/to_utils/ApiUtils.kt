package accountLedgerCli.to_utils

import accountLedgerCli.to_models.IsOkModel

object ApiUtils {

    @JvmStatic
    fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String, serverFileExtension: String): String {

        return "$serverApiMethodName.$serverFileExtension"
    }

    @JvmStatic
    fun <T> makeApiRequestWithOptionalRetries(

        apiCallFunction: () -> Result<T>,
        isDevelopmentMode: Boolean = false

    ): IsOkModel<T> {

        println("Contacting Server...")
        val apiResponse: Result<T> = apiCallFunction.invoke()

        if (isDevelopmentMode) {

            println("API Response : $apiResponse")
        }

        if (apiResponse.isFailure) {

            println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
            do {
                print("Retry (Y/N) ? : ")
                when (readLine()!!) {
                    "Y", "" -> {
                        return makeApiRequestWithOptionalRetries(
                            apiCallFunction = apiCallFunction,
                            isDevelopmentMode = isDevelopmentMode
                        )
                    }

                    "N" -> {
                        return IsOkModel(isOK = false)
                    }

                    else -> println("Invalid option, try again...")
                }
            } while (true)

        } else {

            return IsOkModel(isOK = true, data = apiResponse.getOrNull()!!)
        }
    }
}
