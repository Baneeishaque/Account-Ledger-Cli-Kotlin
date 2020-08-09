package transactionInserterForAccountLedger.cli.app

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class ApiResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : ApiResult<T>()
    data class Error(val exception: Exception) : ApiResult<Nothing>()

    fun isError(): Boolean {

        return when (this) {

            is Error -> true
            else -> false
        }
    }

    fun getValue(): Any {

        return when (this) {

            is Success<*> -> data
            is Error -> exception
        }
    }

    override fun toString(): String {

        return when (this) {

            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}
