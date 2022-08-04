package accountLedgerCli.to_utils

object SentenceUtils {

    fun reverseOrderOfWords(sentence: String) = sentence.split(" ").reversed().joinToString(" ")
}