package gradle.multiplatform.spm.extensions

import gradle.multiplatform.spm.model.defaultConfigVersion
import gradle.multiplatform.spm.model.spm.SpmPackage
import gradle.multiplatform.spm.model.spm.SpmSourceType
import gradle.multiplatform.spm.model.spmKindFieldValue
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.get
import java.io.File
import javax.inject.Inject

open class ProjectFileExtension @Inject constructor(objectFactory: ObjectFactory) {
    lateinit var mainProjectFile: File

    internal val spmPackages: NamedDomainObjectContainer<SpmPackage> =
        objectFactory.domainObjectContainer(SpmPackage::class.java)

    internal var spmConfigVersion = defaultConfigVersion

    fun spmPackage(
        name: String,
        sourceName: String,
        git: String,
        lastCommitHash: String
    ) {
        val existingPackage = spmPackages.findByName(name)
        if (existingPackage == null) {
            spmPackages.create(name) {
                this.sourceName = sourceName
                this.location = git
                this.kind = spmKindFieldValue
                this.revision = lastCommitHash
                this.sourceType = getSourceTypeFromValue(sourceName)
            }
        }
    }

    fun spmPackage(
        sourceName: String,
        git: String
    ) {
//        val existingPackage = spmPackages.findByName(name)
//        if (existingPackage == null) {
//            spmPackages.create(name) {
//                this.version = version
//                location = git
//                kind = spmKindFieldValue
//                revision = lastCommitHash
//            }
//        }
    }

    private fun getSourceTypeFromValue(value: String) = when(value.contains(".")) {
        true -> SpmSourceType.nextMajorVersion
        false -> SpmSourceType.branch
    }
}