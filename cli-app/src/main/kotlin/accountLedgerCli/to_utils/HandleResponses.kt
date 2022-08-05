package accountLedgerCli.to_utils

import accountLedgerCli.to_models.IsOkModel

object HandleResponses {

    @JvmStatic
    internal fun <T> isOkModelHandler(

        isOkModel: IsOkModel<*>,
        data: T,
        successActions: () -> T,
        failureActions: () -> Unit = fun() {},

        ): T {

        var localData: T = data
        if (isOkModel.isOK) {

            localData = successActions.invoke()

        } else {

            failureActions.invoke()
        }
        return localData
    }
}