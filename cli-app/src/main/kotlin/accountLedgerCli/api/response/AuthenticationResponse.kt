package accountLedgerCli.api.response

import com.google.gson.annotations.SerializedName

internal data class AuthenticationResponse(

    internal val id: UInt,
    @SerializedName("user_count") internal val userCount: UInt
)
