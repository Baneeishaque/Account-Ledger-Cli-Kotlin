package accountLedgerCli.cli

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

internal fun getUserInitialTransactionDateFromUsername(username: String): LocalDate {

    return LocalDate.parse(username.removePrefix("banee_ishaque_k_"), DateTimeFormatter.ofPattern("dd_MM_yyyy", Locale.getDefault()))
}