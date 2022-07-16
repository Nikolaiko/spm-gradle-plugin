package gradle.multiplatform.spm.tasks

import gradle.multiplatform.spm.extensions.ProjectFileExtension
import gradle.multiplatform.spm.model.*
import gradle.multiplatform.spm.model.serialization.spm.MainFileContent
import gradle.multiplatform.spm.model.serialization.spm.PackageParameters
import gradle.multiplatform.spm.model.serialization.spm.PackageState
import gradle.multiplatform.spm.model.spm.SpmSourceType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class GenerateSPMConfigTask : DefaultTask() {
    init {
        group = spmGroupName
    }

    @get:Internal
    internal lateinit var filesData: ProjectFileExtension

    @TaskAction
    fun createSpmFile() {
        try {

            val spmFile = filesData.mainProjectFile!!
                .walkTopDown()
                .first { it.isFile && it.name == spmFileName }

            if (filesData.spmPackages.isEmpty()) {
                spmFile.delete()
                return
            }

            val pins = mutableListOf<PackageParameters>()
            filesData.spmPackages.forEach {
                val stats = when(it.sourceType) {
                    SpmSourceType.Branch -> PackageState(branch = it.sourceName, revision = it.revision)
                    SpmSourceType.CurrentVersion -> PackageState(version = it.sourceName, revision = it.revision)
                }

                val pin = PackageParameters(
                    identity = it.identity,
                    kind = it.kind,
                    location = it.location,
                    state = stats
                )
                pins.add(pin)
            }

            val newFileContent = MainFileContent(
                version = filesData.spmConfigVersion,
                pins = pins
            )

            val jsonMapper = Json {
                encodeDefaults = false
                prettyPrint = true
            }
            val stringFileContent = jsonMapper.encodeToString(newFileContent)

            spmFile.writeText(stringFileContent)

            didWork = true

        } catch (notFoundError: NoSuchElementException) {
            didWork = false
            throw GradleException("$notFoundSPMFileException : ${filesData.mainProjectFile?.absolutePath}", notFoundError)
        } catch (nullPointerError: NullPointerException) {
            didWork = false
            throw GradleException(mainFileIsNullException, nullPointerError)
        }
    }
}