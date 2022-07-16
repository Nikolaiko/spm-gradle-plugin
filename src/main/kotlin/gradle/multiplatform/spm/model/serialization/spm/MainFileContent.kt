package gradle.multiplatform.spm.model.serialization.spm

import kotlinx.serialization.Serializable

@Serializable
data class MainFileContent(
    val pins: List<PackageParameters>,
    val version: Int
)