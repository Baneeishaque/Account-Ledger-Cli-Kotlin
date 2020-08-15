package transactionInserterForAccountLedger.api.response

import com.google.gson.annotations.SerializedName

data class AccountResponse(

        @SerializedName("account_id") val id: Int,
        @SerializedName("full_name") val fullName: String,
        val name: String,
        @SerializedName("parent_account_id") val parentAccountId: Int,
        @SerializedName("account_type") val accountType: String,
        val notes: String?,
        @SerializedName("commodity_type") val commodityType: String,
        @SerializedName("commodity_value") val commodityValue: String,
        @SerializedName("owner_id") val ownerId: Int,
        val taxable: String,
        @SerializedName("place_holder") val placeHolder: String
)
