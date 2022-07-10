import gradle.multiplatform.spm.extensions.ProjectFileExtension
import gradle.multiplatform.spm.model.parseSpmFileTaskName
import gradle.multiplatform.spm.model.fileDataExtensionName
import gradle.multiplatform.spm.tasks.ParseConfigTask
import kotlinx.serialization.SerializationException
import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PluginParseTaskTests {
    private val testProject = ProjectBuilder.builder().build()

    @BeforeAll
    fun applyAndConfigurePlugin() {
        testProject.pluginManager.apply(pluginId)

        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        extension.mainProjectFile = File(rightTestProject)
    }

    @AfterEach
    fun resetPluginState() {
        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        extension.mainProjectFile = File(rightTestProject)
    }

    @Test
    fun testSearchOfSPMFile() {
        val task = testProject.tasks.getByName(parseSpmFileTaskName) as ParseConfigTask
        var error: GradleException? = null
        try {
            task.parseSpmFile()
        } catch (exception: GradleException) {
            error = exception
        }
        assertTrue(error == null || error.cause !is NoSuchElementException)
    }

    @Test
    fun testNotFoundSPMFile() {
        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        extension.mainProjectFile = File(wrongTestProject)

        val task = testProject.tasks.getByName(parseSpmFileTaskName) as ParseConfigTask
        var exceptionCause: Throwable? = null

        try {
            task.parseSpmFile()
        } catch (gradleError: GradleException) {
            exceptionCause = gradleError.cause
        }
        assertTrue(exceptionCause is NoSuchElementException)
    }

    @Test
    fun testParsingOfSPMFile() {
        val task = testProject.tasks.getByName(parseSpmFileTaskName) as ParseConfigTask
        assertDoesNotThrow {
            task.parseSpmFile()
        }

        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        assertEquals(extension.spmConfigVersion, testConfigVersion)
        assertEquals(extension.spmPackages.size, testSpmPackagesCount)

        assertNotNull(extension.spmPackages.find {
            it.identity == firstPackageName
        })

        assertNotNull(extension.spmPackages.find {
            it.identity == secondPackageName
        })
    }

    @Test
    fun testWrongFormatOfSPMFile() {
        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        extension.mainProjectFile = File(wrongFormatTestProject)

        val task = testProject.tasks.getByName(parseSpmFileTaskName) as ParseConfigTask
        var exceptionCause: Throwable? = null

        try {
            task.parseSpmFile()
        } catch (gradleError: GradleException) {
            exceptionCause = gradleError.cause
        }
        assertTrue(exceptionCause is SerializationException)
    }
}