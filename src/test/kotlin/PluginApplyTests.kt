import gradle.multiplatform.spm.extensions.ProjectFileExtension
import gradle.multiplatform.spm.model.parseSpmFileTaskName
import gradle.multiplatform.spm.model.fileDataExtensionName
import gradle.multiplatform.spm.tasks.ParseConfigTask
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PluginApplyTests {

    private val testProject = ProjectBuilder.builder().build()

    @BeforeAll
    fun applyPlugin() {
        testProject.pluginManager.apply(pluginId)
    }

    @Test
    fun testTasksExistInProject() {
        assertTrue(testProject.tasks.getByName(parseSpmFileTaskName) is ParseConfigTask)
    }

    @Test
    fun testExtensionsExistInProject() {
        assertTrue(testProject.extensions.getByName(fileDataExtensionName) is ProjectFileExtension)
    }
}