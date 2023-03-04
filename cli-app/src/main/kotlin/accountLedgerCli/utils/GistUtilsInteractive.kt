package accountLedgerCli.utils

import account.ledger.library.constants.Constants
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.utils.TextAccountLedgerUtils
import accountLedgerCli.cli.App
import common.utils.library.utils.GistUtils
import kotlinx.coroutines.runBlocking

object GistUtilsInteractive {

    @JvmStatic
    internal fun processGistId(isDevelopmentMode: Boolean) {

        runBlocking {

            GistUtils.getHttpClientForGitHub(

                accessToken = App.dotenv[EnvironmentFileEntryEnum.GITHUB_TOKEN.name]
                    ?: Constants.defaultValueForStringEnvironmentVariables,
                isDevelopmentMode = isDevelopmentMode

            ).use { client ->

                // TODO: inline use of serialization

                val gistContent: String = GistUtils.getGistContents(

                    client = client,
                    gistId = App.dotenv[EnvironmentFileEntryEnum.GIST_ID.name]
                        ?: Constants.defaultValueForStringEnvironmentVariables,
                    isDevelopmentMode = isDevelopmentMode

                ).files.mainTxt.content

                val gistContentLines: List<String> = gistContent.lines()
                if (App.isDevelopmentMode) {
                    // println("Gist : $gistResponse")
                    println("Gist Contents")
                    // println(gistContent)
                    gistContentLines.forEach { println(it) }
                    println(common.utils.library.constants.Constants.dashedLineSeparator)
                }

                // TODO : Parse Gist Contents
                // var isWalletHeaderFound: Boolean = false
                val accountHeaderIdentifier: String = Constants.accountHeaderIdentifier
                var currentAccountId = 0u
                val processedLedger: LinkedHashMap<UInt, MutableList<String>> = LinkedHashMap()
                var isPreviousLineIsNotAccountHeader = false

                var i = 1u
                gistContentLines.forEach { line: String ->

//                                        println(line)

                    if (line.contains(other = accountHeaderIdentifier)) {

//                                            println("$i : $line")

                        val accountName = line.replace(
                            regex = accountHeaderIdentifier.toRegex(),
                            replacement = ""
                        ).trim()
                        if (accountName == Constants.walletAccountHeaderIdentifier) {

                            // TODO : set currentAccountId from environment variable
                            currentAccountId = 6u
                        }
                        // TODO : check for custom bank name
                        else if (accountName == Constants.bankAccountHeaderIdentifier) {

//                                                      TODO : set currentAccountId from environment variable
                            currentAccountId = 11u
                        }
//                                            println(message = "currentAccountId = $currentAccountId")
                        isPreviousLineIsNotAccountHeader = false

                    } else {

                        if (line.isNotEmpty()) {

//                                                println(line)
                            if (!line.contains(other = Constants.accountHeaderUnderlineCharacter)) {

//                                                    println(line)
                                TextAccountLedgerUtils.addLineToCurrentAccountLedger(
                                    ledgerToProcess = processedLedger,
                                    desiredAccountId = currentAccountId,
                                    desiredLine = line
                                )
                            } else {

                                if (isPreviousLineIsNotAccountHeader) {

//                                                        println("$i : $line")
//                                                        println(line)
                                    TextAccountLedgerUtils.addLineToCurrentAccountLedger(
                                        ledgerToProcess = processedLedger,
                                        desiredAccountId = currentAccountId,
                                        desiredLine = line
                                    )
                                }
                            }
                        }
                        isPreviousLineIsNotAccountHeader = true
                    }
                    i++
                }

                //TODO : Refactor Process Ledger
                processedLedger.forEach { (localCurrentAccountId: UInt, currentAccountLedgerLines: List<String>) ->

                    println("currentAccountId = $localCurrentAccountId")
                    currentAccountLedgerLines.forEach { ledgerLine: String ->

//                                            println(ledgerLine)
                        val indexOfFirstSpace: Int = ledgerLine.indexOf(char = ' ')
                        var dateOrAmount = ""
                        if (indexOfFirstSpace != -1) {

                            dateOrAmount = ledgerLine.substring(
                                startIndex = 0,
                                endIndex = indexOfFirstSpace
                            )
                        } else if (!ledgerLine.contains(other = Constants.dateUnderlineCharacter)) {

                            dateOrAmount = ledgerLine.trim()
                        }
                        if (dateOrAmount.isNotEmpty()) {

                            println(dateOrAmount)
                        }
                    }
                }
            }
        }
    }
}