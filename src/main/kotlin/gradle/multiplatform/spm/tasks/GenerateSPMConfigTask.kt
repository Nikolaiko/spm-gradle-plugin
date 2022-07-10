package gradle.multiplatform.spm.tasks

import gradle.multiplatform.spm.extensions.ProjectFileExtension
import gradle.multiplatform.spm.model.notFoundSPMFileException
import gradle.multiplatform.spm.model.serialization.MainFileContent
import gradle.multiplatform.spm.model.serialization.PackageParameters
import gradle.multiplatform.spm.model.serialization.PackageState
import gradle.multiplatform.spm.model.spm.SpmSourceType
import gradle.multiplatform.spm.model.spmFileName
import gradle.multiplatform.spm.model.spmGroupName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.ObjectMapper

abstract class GenerateSPMConfigTask : DefaultTask() {
    init {
        group = spmGroupName
    }

    @get:Internal
    internal lateinit var filesData: ProjectFileExtension

    @TaskAction
    fun createSpmFile() {
        try {
            val spmFile = filesData.mainProjectFile
                .walkTopDown()
                .first { it.isFile && it.name == spmFileName }

            val pins = mutableListOf<PackageParameters>()
            filesData.spmPackages.forEach {
                val stats = when(it.sourceType) {
                    SpmSourceType.branch -> PackageState(branch = it.sourceName, revision = it.revision)
                    SpmSourceType.nextMajorVersion -> PackageState(version = it.sourceName, revision = it.revision)
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

        } catch (notFoundError: NoSuchElementException) {
            throw GradleException("$notFoundSPMFileException : ${filesData.mainProjectFile.absolutePath}", notFoundError)
        }
    }
}