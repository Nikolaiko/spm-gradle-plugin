package gradle.multiplatform.spm.model.serialization.spm

import kotlinx.serialization.Serializable

@Serializable
data class PackageParameters(
    val identity: String,
    val kind: String,
    val location: String,
    val state: PackageState
)
