package accountLedgerCli.cli

import account.ledger.library.cli.App as LibraryApp

class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            LibraryApp.main(args = args)
        }
    }
}
