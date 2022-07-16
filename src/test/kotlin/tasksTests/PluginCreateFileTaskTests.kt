package tasksTests

import consts.*
import gradle.multiplatform.spm.extensions.ProjectFileExtension
import gradle.multiplatform.spm.model.createSpmFileTaskName
import gradle.multiplatform.spm.model.fileDataExtensionName
import gradle.multiplatform.spm.model.parseSpmFileTaskName
import gradle.multiplatform.spm.tasks.GenerateSPMConfigTask
import gradle.multiplatform.spm.tasks.ParseConfigTask
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*
import rightTestProject
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PluginCreateFileTaskTests {

    private val testProject = ProjectBuilder.builder().build()

    @BeforeAll
    fun applyAndConfigurePlugin() {
        testProject.pluginManager.apply(pluginId)

        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        extension.mainProjectFile = File(rightTestProject)
    }

    @Test
    fun testFileCreatingSPMFile() {
        val parseTask = testProject.tasks.getByName(parseSpmFileTaskName) as ParseConfigTask
        val createTask = testProject.tasks.getByName(createSpmFileTaskName) as GenerateSPMConfigTask
        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension

        assertDoesNotThrow {
            extension.spmPackage(
                name = addingTestIdentity,
                git = addingTestLocation,
                sourceName = addingTestBranch,
                lastCommitHash = addingTestRevision
            )
        }

        assertDoesNotThrow {
            parseTask.parseSpmFile()
        }

        assertDoesNotThrow {
            createTask.createSpmFile()
        }
    }
}