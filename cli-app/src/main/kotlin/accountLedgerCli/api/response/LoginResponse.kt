package accountLedgerCli.api.response

import com.google.gson.annotations.SerializedName

internal data class LoginResponse(

    internal val id: Int,
    @SerializedName("user_count") internal val userCount: Int
)
