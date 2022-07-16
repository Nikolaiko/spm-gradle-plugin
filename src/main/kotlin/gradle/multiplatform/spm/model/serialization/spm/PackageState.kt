package gradle.multiplatform.spm.model.serialization.spm

import kotlinx.serialization.Serializable

@Serializable
data class PackageState(
    val version: String? = null,
    val branch: String? = null,
    val revision: String
)
