cd /workspace
if [ ! -d configurations-private ];then
    git clone https://github.com/Baneeishaque/configurations-private.git
else
    cd configurations-private
    git pull
    cd ..
fi
cd Account-Ledger-Cli-Kotlin-Gradle && ln -s /workspace/configurations-private/AccountLedger/.env && ln -s /workspace/configurations-private/AccountLedger/frequencyOfAccounts.json && ln -s /workspace/configurations-private/AccountLedger/relationOfAccounts.json && cd api && ln -s /workspace/configurations-private/AccountLedger/http-client.env.json