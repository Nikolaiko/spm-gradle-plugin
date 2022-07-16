package gradle.multiplatform.spm.services

import gradle.multiplatform.spm.model.git.GitRepositoryData
import gradle.multiplatform.spm.model.spm.SpmSourceType

interface RepositoryService {
    fun getRepositoryData(url: String, targetName: String, type: SpmSourceType): GitRepositoryData
}