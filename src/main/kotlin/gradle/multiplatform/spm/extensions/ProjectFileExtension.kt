package gradle.multiplatform.spm.extensions

import gradle.multiplatform.spm.model.defaultConfigVersion
import gradle.multiplatform.spm.model.projectProperties.ProjectType
import gradle.multiplatform.spm.model.spm.SpmPackage
import gradle.multiplatform.spm.model.spm.SpmSourceType
import gradle.multiplatform.spm.model.spmKindFieldValue
import gradle.multiplatform.spm.services.repository.GitApiService
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import java.io.File
import javax.inject.Inject

open class ProjectFileExtension @Inject constructor(objectFactory: ObjectFactory) {
    var mainProjectFile: File? = null
    var mainProjectFileType: String = ProjectType.NotSet.name

    internal var projectFileType = ProjectType.NotSet

    internal val spmPackages: NamedDomainObjectContainer<SpmPackage> =
        objectFactory.domainObjectContainer(SpmPackage::class.java)

    internal var spmConfigVersion = defaultConfigVersion

    private val gitService = GitApiService()

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
        val sourceType = getSourceTypeFromValue(sourceName)
        val data = gitService.getRepositoryData(git, sourceName, sourceType)
        val existingPackage = spmPackages.findByName(data.packageIdentity)
        if (existingPackage == null) {
            spmPackages.create(data.packageIdentity) {
                this.sourceName = sourceName
                this.location = git
                this.kind = spmKindFieldValue
                this.revision = data.revisionId
                this.sourceType = sourceType
            }
        }
    }

    private fun getSourceTypeFromValue(value: String) = when(value.contains(".")) {
        true -> SpmSourceType.CurrentVersion
        false -> SpmSourceType.Branch
    }
}