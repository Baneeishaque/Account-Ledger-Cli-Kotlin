package accountLedgerCli.utils

import accountLedgerCli.api.response.UserResponse

object UserUtils {

    internal fun prepareUsersMap(users: List<UserResponse>): LinkedHashMap<Int, UserResponse> {

        val usersMap = LinkedHashMap<Int, UserResponse>()
        users.forEach { currentUser -> usersMap[currentUser.id] = currentUser }
//        return usersMap.toSortedMap()
        return usersMap
    }

    internal fun getBlankUser(): UserResponse {

        return UserResponse(
            id = 0,
            password = "",
            status = "",
            username = ""
        )
    }
}