package flavor.pie.kludgec

import com.google.auto.service.AutoService
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@AutoService(KotlinGradleSubplugin::class)
class KludgeGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {
        val extension = project.extensions.findByType(KludgeGradleExtension::class.java) ?:
                KludgeGradleExtension()
        return listOf(SubpluginOption("onByDefault", extension.optional.onByDefault.toString()))
    }

    override fun getCompilerPluginId(): String = "kludge"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact("com.github.pie-flavor", "KludgeCompiler", "65bd794")

    override fun isApplicable(project: Project, task: AbstractCompile): Boolean =
            project.plugins.hasPlugin(KludgeGradlePlugin::class.java)

}
