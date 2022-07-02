package accountLedgerCli.api.response

internal data class UsersResponse(

    internal val status: Int,
    internal val users: List<UserResponse>
)