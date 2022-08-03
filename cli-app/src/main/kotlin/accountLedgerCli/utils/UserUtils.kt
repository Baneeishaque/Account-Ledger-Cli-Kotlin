package accountLedgerCli.utils

import accountLedgerCli.api.response.UserResponse
import accountLedgerCli.constants.Constants
import accountLedgerCli.models.UserCredentials

object UserUtils {

    @JvmStatic
    internal fun prepareUsersMap(users: List<UserResponse>): LinkedHashMap<UInt, UserResponse> {

        val usersMap = LinkedHashMap<UInt, UserResponse>()
        users.forEach { currentUser -> usersMap[currentUser.id] = currentUser }
//        return usersMap.toSortedMap()
        return usersMap
    }

    internal fun usersToStringFromLinkedHashMap(
        usersMap: LinkedHashMap<UInt, UserResponse>
    ): String {

        var result = ""
        usersMap.forEach { user -> result += "${Constants.userText.first()}${user.key} - ${user.value.username}\n" }
        return result
    }

    @JvmStatic
    internal fun getUserCredentials(): UserCredentials {

        val user = UserCredentials(username = "", passcode = "")
        print("Enter Your Username : ")
        user.username = readLine().toString()
        print("Enter Your Password : ")
        user.passcode = readLine().toString()
        return user
    }
}