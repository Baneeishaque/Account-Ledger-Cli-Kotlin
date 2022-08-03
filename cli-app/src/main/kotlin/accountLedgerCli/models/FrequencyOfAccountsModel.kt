package accountLedgerCli.models

import accountLedgerCli.constants.FrequencyOfAccountsJsonObjectFields
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FrequencyOfAccountsModel(
    @SerialName(FrequencyOfAccountsJsonObjectFields.users)
    var users: List<UserModel>
)