package gradle.multiplatform.spm.model.serialization

import kotlinx.serialization.Serializable

@Serializable
data class PackageState(
    val version: String? = null,
    val branch: String? = null,
    val revision: String
)
