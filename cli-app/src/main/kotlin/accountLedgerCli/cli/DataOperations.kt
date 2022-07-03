package accountLedgerCli.cli

import java.time.LocalDateTime
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

internal fun getUserInitialTransactionDateFromUsername(username: String): LocalDateTime {

    return LocalDateTime.parse(username, DateTimeFormatter.ofPattern("banee_ishaque_k_dd_MM_yyyy", Locale.getDefault()))
}