package gradle.multiplatform.spm.model.serialization.git

import kotlinx.serialization.Serializable

@Serializable
data class GitRecordData(
    val type: String?,
    val encoding: String?,
    val size: Int?,
    val name: String?,
    val path: String?,
    val content: String?,
    val sha: String?,
    val _links: ContentLinks?
)
