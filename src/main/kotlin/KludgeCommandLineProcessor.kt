package flavor.pie.kludgec

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

@AutoService(CommandLineProcessor::class)
class KludgeCommandLineProcessor : CommandLineProcessor {

    init {
        println("CLI processor instantiated")
    }

    override val pluginId: String get() {
        println("Retrieved plugin ID")
        return "kludge"
    }
    override val pluginOptions: Collection<AbstractCliOption> get() {
        println("Retrieved plugin options")
        return listOf(
            CliOption("onByDefault", "<true|false>", "whether optional-to-nullable is on by default", required = false)
        )
    }

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        println("Processing CLI options")
        return when (val name = option.optionName) {
            "onByDefault" -> configuration.put(Keys.ON_BY_DEFAULT, value.toBoolean())
            else -> error("Unexpected config option $name")
        }
    }

}
