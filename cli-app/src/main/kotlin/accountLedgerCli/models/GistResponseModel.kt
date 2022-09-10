package accountLedgerCli.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Root(
    val files: Files
)

@Serializable
internal data class Files(
    // TODO: use environmnent variable for filename
    @SerialName("main.txt")
    val mainTxt: MainTxt
)

@Serializable
internal data class MainTxt(
    val content: String
)