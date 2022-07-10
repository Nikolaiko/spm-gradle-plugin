package gradle.multiplatform.spm.model

const val spmFileName = "Package.resolved"
const val notFoundSPMFileException = "File : $spmFileName was not found in provided root path."
const val parsingFileException = "Error during parsing SPM config file"


const val spmGroupName = "spmTasks"
const val parseSpmFileTaskName = "parseSPMConfigFile"
const val createSpmFileTaskName = "createSPMConfigFile"

const val fileDataExtensionName = "spmProperties"

const val spmKindFieldValue = "remoteSourceControl"
const val defaultConfigVersion = 2