# git submodule update --init --recursive || exit
cd /workspace || exit
if [ ! -d configurations-private ];then
    git clone https://github.com/Baneeishaque/configurations-private.git
else
    cd configurations-private || exit
    git pull
    cd ..
fi
cd Account-Ledger-Cli-Kotlin && rm .env && rm frequencyOfAccounts.json && rm relationOfAccounts.json && rm api/http-client.env.json && ln -s /workspace/configurations-private/AccountLedger/.env .env && ln -s /workspace/configurations-private/AccountLedger/frequencyOfAccounts.json frequencyOfAccounts.json && ln -s /workspace/configurations-private/AccountLedger/relationOfAccounts.json relationOfAccounts.json && cd api && ln -s /workspace/configurations-private/AccountLedger/http-client.env.json http-client.env.json