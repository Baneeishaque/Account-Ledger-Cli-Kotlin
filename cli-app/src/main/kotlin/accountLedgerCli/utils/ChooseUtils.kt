package accountLedgerCli.utils

import accountLedgerCli.models.ChooseByIdResult
import accountLedgerCli.to_models.IsOkModel
import java.util.*
import accountLedgerCli.to_utils.ApiUtils as CommonApiUtils

internal object ChooseUtils {

    @JvmStatic
    internal fun <T> chooseById(

        itemSpecification: String,
        apiCallFunction: () -> Result<T>,
        prefixForPrompt: String = ""

    ): ChooseByIdResult<T> {

        var idInput: UInt
        val reader = Scanner(System.`in`)

        while (true) {

            print("Enter $prefixForPrompt$itemSpecification ID or 0 to Back : ")

            try {

                idInput = reader.nextInt().toUInt()
                if (idInput == 0u) return ChooseByIdResult(isOkWithData = IsOkModel(isOK = false))

                return ChooseByIdResult(
                    isOkWithData = CommonApiUtils.makeApiRequestWithOptionalRetries(apiCallFunction = apiCallFunction),
                    id = idInput
                )

            } catch (exception: InputMismatchException) {

                println("Invalid $itemSpecification ID...")
            }
        }
    }
}