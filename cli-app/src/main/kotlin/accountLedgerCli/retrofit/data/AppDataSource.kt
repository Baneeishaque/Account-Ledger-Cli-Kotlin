package accountLedgerCli.retrofit.data

import accountLedgerCli.api.Api
import accountLedgerCli.retrofit.ProjectRetrofitClient

internal open class AppDataSource<T : Any>(val retrofitClient: Api = ProjectRetrofitClient.retrofitClient) :
    CommonDataSource<T>()