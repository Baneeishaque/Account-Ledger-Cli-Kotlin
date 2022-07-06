package accountLedgerCli.cli

import accountLedgerCli.cli.App.Companion.fromAccount
import accountLedgerCli.cli.App.Companion.toAccount
import accountLedgerCli.cli.App.Companion.viaAccount
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

internal fun exchangeFromAndViaAccounts() {

    val tempAccount = fromAccount
    fromAccount = viaAccount
    viaAccount = tempAccount
}

internal fun exchangeFromAndToAccounts() {

    val tempAccount = fromAccount
    fromAccount = toAccount
    toAccount = tempAccount
}

internal fun exchangeToAndViaAccounts() {

    val tempAccount = toAccount
    toAccount = viaAccount
    viaAccount = tempAccount
}

internal fun getUserInitialTransactionDateFromUsername(username: String): LocalDate {

    return LocalDate.parse(username.removePrefix("banee_ishaque_k_"), DateTimeFormatter.ofPattern("dd_MM_yyyy", Locale.getDefault()))
}