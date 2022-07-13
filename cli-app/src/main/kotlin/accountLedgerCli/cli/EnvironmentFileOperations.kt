package accountLedgerCli.cli

import io.github.cdimascio.dotenv.Dotenv
import java.lang.NumberFormatException

internal open class EnvironmentVariableForAny<T>(val isAvailable: Boolean, val value: T? = null)
internal class EnvironmentVariableForWholeNumber(isAvailable: Boolean, value: UInt? = null) :
    EnvironmentVariableForAny<UInt>(isAvailable, value)

object EnvironmentFileOperations {
    internal fun getEnvironmentVariableValueForTextWithDefaultValue(

        dotenv: Dotenv,
        environmentVariableName: String,
        defaultValue: String

    ): String = (dotenv[environmentVariableName] ?: defaultValue).trim('\'')

    internal fun getEnvironmentVariableValueForWholeNumberWithDefaultValue(

        dotenv: Dotenv,
        environmentVariableName: String,
        environmentVariableFormalName: String,
        defaultValue: UInt

    ): EnvironmentVariableForWholeNumber {

        val result: String = dotenv[environmentVariableName]
        return if (result.isEmpty()) {

            EnvironmentVariableForWholeNumber(isAvailable = true, value = defaultValue)

        } else {

            try {

                EnvironmentVariableForWholeNumber(isAvailable = true, value = result.toUInt())

            } catch (exception: NumberFormatException) {

                print("Invalid $environmentVariableFormalName (Environment File)")
                EnvironmentVariableForWholeNumber(isAvailable = false)
            }
        }
    }

    internal fun getEnvironmentVariableValueForWholeNumber(

        dotenv: Dotenv,
        environmentVariableName: String,
        environmentVariableFormalName: String

    ): EnvironmentVariableForWholeNumber {

        val result: String = dotenv[environmentVariableName]
        return if (result.isEmpty()) {

            print("Please specify $environmentVariableFormalName (Environment File)")
            EnvironmentVariableForWholeNumber(isAvailable = false)

        } else {

            try {

                EnvironmentVariableForWholeNumber(isAvailable = true, value = result.toUInt())

            } catch (exception: NumberFormatException) {

                print("Invalid $environmentVariableFormalName (Environment File)")
                EnvironmentVariableForWholeNumber(isAvailable = false)
            }
        }
    }

    internal fun isEnvironmentVariablesAreAvailable(environmentVariables: List<EnvironmentVariableForAny<*>>): Boolean {

        return environmentVariables.all { it.isAvailable }
    }
}
