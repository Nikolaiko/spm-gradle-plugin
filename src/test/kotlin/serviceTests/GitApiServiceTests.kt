package serviceTests

import consts.*
import gradle.multiplatform.spm.model.git.GitRepositoryData
import gradle.multiplatform.spm.model.spm.SpmSourceType
import gradle.multiplatform.spm.services.repository.GitApiService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitApiServiceTests {

    private val service = GitApiService()

    @Test
    fun testGetPackageIdentityByBranchName() {
        var gitData: GitRepositoryData? = null

        assertDoesNotThrow {
            gitData = service.getRepositoryData(testPackageRepoUrl, testBranchName, SpmSourceType.Branch)
        }
        Assertions.assertNotNull(gitData)
        Assertions.assertEquals(gitData!!.packageIdentity, testIdentityName)
        Assertions.assertEquals(gitData!!.revisionId, testRevision)
    }

    @Test
    fun testGetPackageIdentityByVersionName() {
        var gitData: GitRepositoryData? = null

        assertDoesNotThrow {
            gitData = service.getRepositoryData(testPackageRepoUrl, testTagName, SpmSourceType.CurrentVersion)
        }
        Assertions.assertNotNull(gitData)
        Assertions.assertEquals(gitData!!.packageIdentity, testIdentityName)
        Assertions.assertEquals(gitData!!.revisionId, testRevision)
    }
}