package gradle.multiplatform.spm.tasks

import gradle.multiplatform.spm.extensions.ProjectFileExtension
import gradle.multiplatform.spm.model.*
import gradle.multiplatform.spm.model.projectProperties.ProjectType
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class ValidateSettingsTask : DefaultTask() {
    init {
        group = spmGroupName
    }

    @get:Internal
    internal lateinit var filesData: ProjectFileExtension

    @TaskAction
    fun validateSettings() {
        if (filesData.mainProjectFile == null) {
            didWork = false
            throw GradleException(setMainFileExceptionMessage, NullPointerException(mainFileIsNullException))
        }

        when (filesData.mainProjectFileType == ProjectType.NotSet.name) {
            true -> {
                didWork = false
                throw GradleException(setMainFileTypeExceptionMessage)
            }
            false -> parseProjectTypeString()
        }
        didWork = true
    }

    private fun parseProjectTypeString() {
        try {
            filesData.projectFileType = ProjectType.valueOf(filesData.mainProjectFileType)
        } catch(wrongValueError: IllegalArgumentException) {
            didWork = false
            throw GradleException(wrongFileTypeValueExceptionMessage, wrongValueError)
        }
    }
}