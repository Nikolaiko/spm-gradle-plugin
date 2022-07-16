package tasksTests

import consts.pluginId
import gradle.multiplatform.spm.extensions.ProjectFileExtension
import gradle.multiplatform.spm.model.*
import gradle.multiplatform.spm.model.projectProperties.ProjectType
import gradle.multiplatform.spm.tasks.ValidateSettingsTask
import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import rightTestProject
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidationTaskTests {
    private val testProject = ProjectBuilder.builder().build()

    @BeforeAll
    fun applyAndConfigurePlugin() {
        testProject.pluginManager.apply(pluginId)
    }

    @BeforeEach
    fun resetState() {
        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        extension.mainProjectFileType = ProjectType.NotSet.name
        extension.mainProjectFile = null
    }

    @Test
    fun testNotSetFilePath() {
        val task = testProject.tasks.getByName(validateTaskName) as ValidateSettingsTask
        val exception = assertThrows<GradleException>(setMainFileExceptionMessage) {
            task.validateSettings()
        }
        assertTrue(exception.cause is NullPointerException)
    }

    @Test
    fun testNotSetFileType() {
        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        extension.mainProjectFile = File(rightTestProject)

        val task = testProject.tasks.getByName(validateTaskName) as ValidateSettingsTask
        assertThrows<GradleException>(setMainFileTypeExceptionMessage) {
            task.validateSettings()
        }
    }

    @Test
    fun testWrongFileType() {
        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        extension.mainProjectFile = File(rightTestProject)
        extension.mainProjectFileType = "AnyErrorValue"

        val task = testProject.tasks.getByName(validateTaskName) as ValidateSettingsTask
        val exception = assertThrows<GradleException>(wrongFileTypeValueExceptionMessage) {
            task.validateSettings()
        }
        assertTrue(exception.cause is IllegalArgumentException)
    }

    @Test
    fun testValidSettings() {
        val extension = testProject.extensions.getByName(fileDataExtensionName) as ProjectFileExtension
        extension.mainProjectFile = File(rightTestProject)
        extension.mainProjectFileType = ProjectType.ProjectFile.name

        val task = testProject.tasks.getByName(validateTaskName) as ValidateSettingsTask
        assertDoesNotThrow {
            task.validateSettings()
        }
        assertEquals(extension.projectFileType, ProjectType.ProjectFile)
    }
}