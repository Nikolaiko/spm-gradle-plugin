package gradle.multiplatform.spm.model.serialization.git.tagsData

import gradle.multiplatform.spm.model.serialization.git.common.GitCommitInsideItemData
import kotlinx.serialization.Serializable

@Serializable
data class GitTagData(
    val name: String,
    val commit: GitCommitInsideItemData
)
