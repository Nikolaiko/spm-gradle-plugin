import gradle.multiplatform.spm.extensions.ProjectFileExtension
import gradle.multiplatform.spm.model.fileDataExtensionName
import gradle.multiplatform.spm.model.spm.SpmSourceType
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PluginFileExtensionTests {
    private val testProject = ProjectBuilder.builder().build()

    @BeforeAll
    fun applyAndConfigurePlugin() {
        testProject.pluginManager.apply(pluginId)

        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        extension.mainProjectFile = File(rightTestProject)
    }

    @Test
    fun testAddingSPM() {
        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension

        assertDoesNotThrow {
            extension.spmPackage(
                name = addingTestIdentity,
                git = addingTestLocation,
                sourceName = addingTestBranch,
                lastCommitHash = addingTestRevision
            )
        }

        assertEquals(extension.spmPackages.size, 1)
        assertNotNull(extension.spmPackages.find {
            it.identity == addingTestIdentity &&
                    it.sourceName == addingTestBranch &&
                    it.location == addingTestLocation &&
                    it.revision == addingTestRevision &&
                    it.sourceType == SpmSourceType.branch
        })
    }
}