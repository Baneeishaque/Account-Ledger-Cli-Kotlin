package accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.commandLinePrintMenuDefinedPrompt

import accountLedgerCli.toUtils.printMenu.commandLinePrintMenu.prompt.TryPromptInterface

public interface CommandLinePrintMenuWithTryPromptInterface: CommandLinePrintMenuDefinedPromptInterface, TryPromptInterface {
    override val promptWord: String
        get() = promptWord
}