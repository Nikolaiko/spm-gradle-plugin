package gradle.multiplatform.spm

import gradle.multiplatform.spm.extensions.ProjectFileExtension
import gradle.multiplatform.spm.model.createSpmFileTaskName
import gradle.multiplatform.spm.model.parseSpmFileTaskName
import gradle.multiplatform.spm.model.fileDataExtensionName
import gradle.multiplatform.spm.model.validateTaskName
import gradle.multiplatform.spm.tasks.GenerateSPMConfigTask
import gradle.multiplatform.spm.tasks.ParseConfigTask
import gradle.multiplatform.spm.tasks.ValidateSettingsTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class MultiplatformSPMPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create(fileDataExtensionName, ProjectFileExtension::class.java)

        val validateTask = target.tasks.register(validateTaskName, ValidateSettingsTask::class.java) {
            filesData = extension
        }

        val parseTask = target.tasks.register(parseSpmFileTaskName, ParseConfigTask::class.java) {
            filesData = extension
            dependsOn(validateTask.get())
        }

        target.tasks.register(createSpmFileTaskName, GenerateSPMConfigTask::class.java) {
            filesData = extension
            dependsOn(parseTask.get())
        }
    }
}