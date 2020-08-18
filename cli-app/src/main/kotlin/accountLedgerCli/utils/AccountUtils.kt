package accountLedgerCli.utils

import accountLedgerCli.api.response.AccountResponse

internal object AccountUtils {

    internal fun getBlankAccount(): AccountResponse {

        return AccountResponse(
                id = 0,
                fullName = "",
                name = "",
                parentAccountId = 0,
                accountType = "",
                notes = "",
                commodityType = "",
                commodityValue = "",
                ownerId = 0,
                taxable = "",
                placeHolder = ""
        )
    }
}
