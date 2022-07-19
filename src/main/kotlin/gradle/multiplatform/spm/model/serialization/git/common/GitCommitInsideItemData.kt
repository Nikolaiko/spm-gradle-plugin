package gradle.multiplatform.spm.model.serialization.git.common

import kotlinx.serialization.Serializable

@Serializable
data class GitCommitInsideItemData(
    val sha: String,
    val url: String
)
