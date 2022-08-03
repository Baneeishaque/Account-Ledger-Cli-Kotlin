package accountLedgerCli.models

import accountLedgerCli.constants.UserJsonObjectFields
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserModel(
    @SerialName(UserJsonObjectFields.id)
    val id: UInt,
    @SerialName(UserJsonObjectFields.accountFrequencies)
    var accountFrequencies: List<AccountFrequencyModel>
)