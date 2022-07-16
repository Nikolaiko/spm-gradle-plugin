package serviceTests

import consts.*
import gradle.multiplatform.spm.model.git.GitRepositoryData
import gradle.multiplatform.spm.model.spm.SpmSourceType
import gradle.multiplatform.spm.services.KGitService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KGitServiceTests {

    private val service = KGitService()

    @Test
    fun testGetPackageIdentityByVersion() {
        var gitData: GitRepositoryData? = null

        assertDoesNotThrow {
            gitData = service.getRepositoryData(testPackageRepoUrl, testTagName, SpmSourceType.CurrentVersion)
        }
        assertNotNull(gitData)
        assertEquals(gitData!!.packageIdentity, testIdentityName)
        assertEquals(gitData!!.revisionId, testRevision)
    }

    @Test
    fun testGetPackageIdentityByBranchName() {
        var gitData: GitRepositoryData? = null

        assertDoesNotThrow {
            gitData = service.getRepositoryData(testPackageRepoUrl, testBranchName, SpmSourceType.Branch)
        }
        assertNotNull(gitData)
        assertEquals(gitData!!.packageIdentity, testIdentityName)
        assertEquals(gitData!!.revisionId, testRevision)
    }
}