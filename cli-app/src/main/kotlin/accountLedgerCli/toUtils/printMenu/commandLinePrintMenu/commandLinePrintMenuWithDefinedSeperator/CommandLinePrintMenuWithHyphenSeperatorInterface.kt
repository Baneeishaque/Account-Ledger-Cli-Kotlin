package accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.commandLinePrintMenuWithDefinedSeperator

import accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.prompt.HyphenSeperatorInterface

public interface CommandLinePrintMenuWithHyphenSeperatorInterface: CommandLinePrintMenuWithDefinedSeperatorInterface, HyphenSeperatorInterface {
    override val titleSeperator: Char
        get() = titleSeperator
}