package transactionInserterForAccountLedger.cli.app

import com.google.gson.annotations.SerializedName

data class AccountLedgerApiLoginResponse (

    val id : Int,
    @SerializedName("user_count") val userCount : Int
)
