package transactionInserterForAccountLedger.cli.app

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AccountLedgerRetrofitClient {

    val retrofitClient: AccountLedgerApi by lazy {

        Retrofit.Builder()
                .baseUrl(AccountLedgerApiConstants.serverApiAddress)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build().create(AccountLedgerApi::class.java)
    }
}
