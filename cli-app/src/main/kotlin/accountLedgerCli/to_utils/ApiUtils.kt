package accountLedgerCli.to_utils

import accountLedgerCli.to_models.IsOkModel

object ApiUtils {

    @JvmStatic
    fun getServerApiMethodAbsoluteUrl(serverApiMethodName: String, serverFileExtension: String): String {

        return "$serverApiMethodName.$serverFileExtension"
    }

    @JvmStatic
    fun <T> makeApiRequestWithOptionalRetries(

        apiCallFunction: () -> Result<T>

    ): IsOkModel<T> {

        val apiResponse: Result<T> = apiCallFunction.invoke()
        if (apiResponse.isFailure) {

            println("Error : ${(apiResponse.exceptionOrNull() as Exception).localizedMessage}")
            do {
                print("Retry (Y/N) ? : ")
                when (readLine()!!) {
                    "Y", "" -> {
                        return makeApiRequestWithOptionalRetries(
                            apiCallFunction = apiCallFunction
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
