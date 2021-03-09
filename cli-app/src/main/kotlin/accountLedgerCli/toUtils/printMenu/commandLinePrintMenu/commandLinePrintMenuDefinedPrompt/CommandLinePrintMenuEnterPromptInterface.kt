package accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.commandLinePrintMenuDefinedPrompt

import accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.prompt.EnterPromptInterface

public interface CommandLinePrintMenuWithEnterPromptInterface: CommandLinePrintMenuDefinedPromptInterface, EnterPromptInterface {
    override val promptWord: String
        get() = promptWord
}