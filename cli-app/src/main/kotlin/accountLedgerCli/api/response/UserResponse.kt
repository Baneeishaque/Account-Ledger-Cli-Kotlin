package accountLedgerCli.api.response

internal data class UserResponse(
    internal val id: Int,
    internal val password: String,
    internal val status: String,
    internal val username: String
)