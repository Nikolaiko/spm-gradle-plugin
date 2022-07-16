package gradle.multiplatform.spm.model.serialization.git

import kotlinx.serialization.Serializable

@Serializable
data class ContentLinks(
    val git: String?,
    val self: String?,
    val html: String?
)
