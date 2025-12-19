#!/bin/bash
set -e

# Rebuild Tar Distribution logic
cd cli-app/build/distributions
tar -xvf cli-app.tar
mv cli-app Account-Ledger-Cli
cd Account-Ledger-Cli/bin
mv cli-app Account-Ledger-Cli
chmod a+x Account-Ledger-Cli
mv cli-app.bat Account-Ledger-Cli.bat
echo -e "[tools]\njava = \"23.0.1\"" > mise.toml
cd ../..
tar -czvf Account-Ledger-Cli.tar.gz Account-Ledger-Cli
