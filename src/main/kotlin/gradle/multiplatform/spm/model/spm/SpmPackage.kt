package gradle.multiplatform.spm.model.spm

open class SpmPackage(val name: String) {
    var identity: String = name.toLowerCase()
    var kind: String = ""
    var location: String = ""
    var sourceName: String = ""
    var revision: String = ""
    var sourceType: SpmSourceType = SpmSourceType.Branch
}
