package flavor.pie.kludgec

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class KludgeGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create("kludge", KludgeGradleExtension::class.java)
    }

}

open class KludgeGradleExtension {

    class OptionalBlock {

        var onByDefault: Boolean = true
        var annotations: MutableList<String> = mutableListOf()

    }

    val optional = OptionalBlock()

    fun optional(action: Action<in OptionalBlock>) {
        action.execute(optional)
    }

}
