./gradlew distTar
cd cli-app/build/distributions
tar -xvf cli-app.tar
mv cli-app Account-Ledger-Cli
cd Account-Ledger-Cli/bin
mv cli-app Account-Ledger-Cli
chmod a+x Account-Ledger-Cli
mv cli-app.bat Account-Ledger-Cli.bat
cd ../..
tar -czvf Account-Ledger-Cli.tar.gz Account-Ledger-Cli
rm cli-app.tar
rm -r Account-Ledger-Cli