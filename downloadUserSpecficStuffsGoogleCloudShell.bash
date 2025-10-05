cd ~/cloudshell_open \
 && git clone https://github.com/Baneeishaque/configurations-private.git \
 && cd Account-Ledger-Cli-Kotlin-Gradle \
 && ln -s ~/cloudshell_open/configurations-private/AccountLedger/.env . \
 && ln -s ~/cloudshell_open/configurations-private/AccountLedger/frequencyOfAccounts.json . \
 && cd api && ln -s ~/cloudshell_open/configurations-private/AccountLedger/http-client.env.json .
