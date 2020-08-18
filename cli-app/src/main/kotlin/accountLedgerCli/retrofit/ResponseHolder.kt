package accountLedgerCli.retrofit

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class ResponseHolder<out T : Any> {

    data class Success<out T : Any>(val data: T) : ResponseHolder<T>()
    data class Error(val exception: Exception) : ResponseHolder<Nothing>()

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
