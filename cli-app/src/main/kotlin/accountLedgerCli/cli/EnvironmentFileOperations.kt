package accountLedgerCli.cli

import io.github.cdimascio.dotenv.Dotenv

internal fun getEnvironmentVariableValue(dotenv: Dotenv, environmentVariableName: String, defaultValue: String) =
    (dotenv[environmentVariableName] ?: defaultValue).trim('\'')