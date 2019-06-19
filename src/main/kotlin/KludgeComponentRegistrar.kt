package flavor.pie.kludgec

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

@AutoService(ComponentRegistrar::class)
class KludgeComponentRegistrar : ComponentRegistrar {

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[Keys.ON_BY_DEFAULT] == false) {
            return
        }
        println("Registering components")
        ClassBuilderInterceptorExtension.registerExtension(project, KludgeClassGenerationInterceptor())
    }

}

