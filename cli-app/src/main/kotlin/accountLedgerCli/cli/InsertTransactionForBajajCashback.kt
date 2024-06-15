package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.enums.BajajDiscountTypeEnum
import account_ledger_library.enums.BajajRewardTypeEnum
import account.ledger.library.enums.EnvironmentFileEntryEnum
import account.ledger.library.models.TransactionModel
import account.ledger.library.utils.TransactionForBajajUtils
import account_ledger_library.constants.ConstantsNative
import common.utils.library.models.EnvironmentFileEntryStrictModel
import common.utils.library.models.IsOkModel
import io.github.cdimascio.dotenv.Dotenv

object InsertTransactionForBajajCashback {

    fun generateTransactionsForBajajSubWallet(

        isFundingTransactionPresent: Boolean,
        sourceAccount: AccountResponse,
        secondPartyAccount: AccountResponse,
        eventDateTimeInText: String,
        dotEnv: Dotenv,
        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean,
        isBalanceCheckByPassed: Boolean,
        discountType: BajajDiscountTypeEnum,
        upToValue: UInt?

    ): IsOkModel<List<TransactionModel>> = TransactionForBajajUtils.generateTransactionsForRewardBase(

        isFundingTransactionPresent = isFundingTransactionPresent,
        sourceAccount = sourceAccount,
        secondPartyAccount = secondPartyAccount,
        eventDateTimeInText = eventDateTimeInText,
        dotEnv = dotEnv,
        userId = userId,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode,
        isBalanceCheckByPassed = isBalanceCheckByPassed,
        rewardIncomeAccountIdEnvironmentVariable = EnvironmentFileEntryStrictModel(

            entry = EnvironmentFileEntryEnum.BAJAJ_CASHBACK_INCOME_ACCOUNT_ID,
            formalName = ConstantsNative.BAJAJ_CASHBACK_INCOME_ACCOUNT_ID_TEXT
        ),
        rewardCollectionAccountIdEnvironmentVariable = EnvironmentFileEntryStrictModel(

            entry = EnvironmentFileEntryEnum.BAJAJ_CASHBACK_ACCOUNT_ID,
            formalName = ConstantsNative.BAJAJ_CASHBACK_ACCOUNT_ID_TEXT
        ),
        rewardType = BajajRewardTypeEnum.CASHBACK,
        discountType = discountType,
        upToValue = upToValue
    )
}
