package gradle.multiplatform.spm.model.serialization.git

import kotlinx.serialization.Serializable

@Serializable
data class GitCommitData(
    val sha: String,
    val node_id: String
)
