package accountLedgerCli.retrofit.data

import retrofit2.Response

open class CommonDataSource<T : Any> {

    fun processApiResponse(apiResponse: Response<T>): Result<T> {

        if (apiResponse.isSuccessful) {

            val loginApiResponseBody: T? = apiResponse.body()
            return if (loginApiResponseBody == null) {

                Result.failure(exception = Exception("Response Body is Null"))

            } else {

                Result.success(value = loginApiResponseBody)
            }
        }
        return Result.failure(exception = Exception("Exception Code - ${apiResponse.code()}, Message - ${apiResponse.message()}"))
    }

    fun executeApiRequest(apiRequest: Response<T>): Result<T> {

        return try {

            processApiResponse(apiResponse = apiRequest)

        } catch (exception: java.lang.Exception) {

            Result.failure(exception = Exception("Exception - ${exception.localizedMessage}"))
        }
    }
}