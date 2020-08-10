package transactionInserterForAccountLedger.api.response

import com.google.gson.annotations.SerializedName

data class LoginResponse (

    val id : Int,
    @SerializedName("user_count") val userCount : Int
)
