package gradle.multiplatform.spm.tasks

import gradle.multiplatform.spm.extensions.ProjectFileExtension
import gradle.multiplatform.spm.model.notFoundSPMFileException
import gradle.multiplatform.spm.model.parsingFileException
import gradle.multiplatform.spm.model.serialization.MainFileContent
import gradle.multiplatform.spm.model.spmFileName
import gradle.multiplatform.spm.model.spmGroupName
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import kotlin.reflect.typeOf


abstract class ParseConfigTask : DefaultTask() {

    init {
        group = spmGroupName
    }

    @get:Internal
    internal lateinit var filesData: ProjectFileExtension

    @TaskAction
    fun parseSpmFile() {
        try {
            val spmFile = filesData.mainProjectFile
                .walkTopDown()
                .first { it.isFile && it.name == spmFileName }

            val fileContent = spmFile.readText()
            val projectPackages = Json.decodeFromString<MainFileContent>(fileContent)

            filesData.spmConfigVersion = projectPackages.version
            projectPackages.pins.forEach {
                filesData.spmPackage(
                    name = it.identity,
                    git = it.location,
                    lastCommitHash = it.state.revision,
                    sourceName = when(it.state.branch == null) {
                        true -> it.state.version!!
                        false -> it.state.branch
                    }
                )
            }
        } catch (notFoundError: NoSuchElementException) {
            throw GradleException("$notFoundSPMFileException : ${filesData.mainProjectFile.absolutePath}", notFoundError)
        } catch (parsingError: SerializationException) {
            throw GradleException(parsingError.message ?: parsingFileException, parsingError)
        }
    }
}