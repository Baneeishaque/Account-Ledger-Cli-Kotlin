package accountLedgerCli.models

import accountLedgerCli.constants.AccountFrequencyJsonObjectFields
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AccountFrequencyModel(
    @SerialName(AccountFrequencyJsonObjectFields.accountID)
    val accountID: UInt,
    @SerialName(AccountFrequencyJsonObjectFields.accountName)
    val accountName: String,
    @SerialName(AccountFrequencyJsonObjectFields.countOfRepetition)
    var countOfRepetition: UInt
)