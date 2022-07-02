package accountLedgerCli.cli

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
