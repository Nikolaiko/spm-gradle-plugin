package gradle.multiplatform.spm.model.serialization.git.branchData

import gradle.multiplatform.spm.model.serialization.git.common.GitCommitInsideItemData
import kotlinx.serialization.Serializable

@Serializable
data class GitBranchData(
    val name: String,
    val commit: GitCommitInsideItemData
)
