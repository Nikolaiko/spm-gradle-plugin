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
class FacebookExampleTests {
    private val service = GitApiService()

    @Test
    fun testGetPackageIdentityByVersionName() {
        var gitData: GitRepositoryData? = null

        assertDoesNotThrow {
            gitData = service.getRepositoryData(fbTestPackageRepoUrl, fbTestTagName, SpmSourceType.CurrentVersion)
        }
        Assertions.assertNotNull(gitData)
        Assertions.assertEquals(gitData!!.packageIdentity, fbTestIdentityName)
        Assertions.assertEquals(gitData!!.revisionId, fbTestRevision)
    }
}