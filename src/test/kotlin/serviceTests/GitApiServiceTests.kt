package serviceTests

import consts.testBranchName
import consts.testIdentityName
import consts.testPackageRepoUrl
import consts.testRevision
import gradle.multiplatform.spm.model.git.GitRepositoryData
import gradle.multiplatform.spm.model.spm.SpmSourceType
import gradle.multiplatform.spm.services.GitApiService
import gradle.multiplatform.spm.services.KGitService
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
}