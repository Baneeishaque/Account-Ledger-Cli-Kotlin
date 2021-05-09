package accountLedgerCli.api.response

import com.google.gson.annotations.SerializedName

internal data class AccountResponse(

    @SerializedName("account_id") internal val id: Int,
    @SerializedName("full_name") internal val fullName: String,
    internal val name: String,
    @SerializedName("parent_account_id") internal val parentAccountId: Int,
    @SerializedName("account_type") internal val accountType: String,
    internal val notes: String?,
    @SerializedName("commodity_type") internal val commodityType: String,
    @SerializedName("commodity_value") internal val commodityValue: String,
    @SerializedName("owner_id") internal val ownerId: Int,
    internal val taxable: String,
    @SerializedName("place_holder") internal val placeHolder: String
)
