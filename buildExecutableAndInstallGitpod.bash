./gradlew distTar
cd cli-app/build/distributions || exit
tar -xvf cli-app.tar
mv cli-app Account-Ledger-Cli
cd Account-Ledger-Cli/bin || exit
mv cli-app Account-Ledger-Cli
chmod a+x Account-Ledger-Cli
mv cli-app.bat Account-Ledger-Cli.bat
cd ../..
rm -r /workspace/Account-Ledger-Cli
mv Account-Ledger-Cli /workspace/
cd /workspace || exit
if [ ! -d configurations-private ];then
    git clone https://github.com/Baneeishaque/configurations-private.git
else
    cd configurations-private || exit
    git pull
    cd ..
fi
cd Account-Ledger-Cli/bin && ln -s /workspace/configurations-private/AccountLedger/.env . && ln -s /workspace/configurations-private/AccountLedger/frequencyOfAccounts.json . && ln -s /workspace/configurations-private/AccountLedger/relationOfAccounts.json .
