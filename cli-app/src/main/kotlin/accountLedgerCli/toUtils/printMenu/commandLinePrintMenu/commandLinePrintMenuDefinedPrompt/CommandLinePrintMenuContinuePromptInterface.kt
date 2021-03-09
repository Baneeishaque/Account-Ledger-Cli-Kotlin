package accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.commandLinePrintMenuDefinedPrompt

import accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.prompt.ContinuePromptInterface

public interface CommandLinePrintMenuContinuePromptInterface: CommandLinePrintMenuDefinedPromptInterface, ContinuePromptInterface {
    override val promptWord: String
        get() = promptWord
}