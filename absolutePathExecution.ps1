Remove-Item Account-Ledger-Cli -Recurse
mkdir Account-Ledger-Cli
tar -xvf cli-app\build\distributions\cli-app.tar -C Account-Ledger-Cli\
timeout 5
cp .env Account-Ledger-Cli\cli-app\bin\
jabba use openjdk@21.0.1
Account-Ledger-Cli\cli-app\bin\cli-app.bat BalanceSheet