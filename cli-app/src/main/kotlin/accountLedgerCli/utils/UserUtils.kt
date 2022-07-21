package accountLedgerCli.utils

import accountLedgerCli.api.response.UserResponse

object UserUtils {

    @JvmStatic
    internal fun prepareUsersMap(users: List<UserResponse>): LinkedHashMap<UInt, UserResponse> {

        val usersMap = LinkedHashMap<UInt, UserResponse>()
        users.forEach { currentUser -> usersMap[currentUser.id] = currentUser }
//        return usersMap.toSortedMap()
        return usersMap
    }

    @JvmStatic
    internal val blankUser: UserResponse = UserResponse(
        id = 0u,
        password = "",
        status = "",
        username = ""
    )
}