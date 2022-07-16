package gradle.multiplatform.spm.model

import gradle.multiplatform.spm.model.projectProperties.ProjectType

const val spmFileName = "Package.resolved"
const val mainFilePathProperty = "mainProjectFile"
const val mainFileTypeProperty = "projectFileType"

const val errorDuringGitParsing = "Error during parsing git repo."
const val notFoundSPMFileException = "File : $spmFileName was not found in provided root path."
const val parsingFileException = "Error during parsing SPM config file"
const val mainFileIsNullException = "Main project file setting : $mainFilePathProperty is null"
const val setMainFileExceptionMessage = "$mainFileIsNullException. You need to set property : $mainFilePathProperty"
val setMainFileTypeExceptionMessage = "Property : $mainFileTypeProperty. You need to set property : $mainFileTypeProperty to ${ProjectType.ProjectFile.name} or ${ProjectType.Workspace.name}."
val wrongFileTypeValueExceptionMessage = "Property : $mainFileTypeProperty have wrong value. You need to set property : $mainFileTypeProperty to ${ProjectType.ProjectFile.name} or ${ProjectType.Workspace.name}."

const val spmGroupName = "spmTasks"
const val validateTaskName = "validatePluginConfig"
const val parseSpmFileTaskName = "parseSPMConfigFile"
const val createSpmFileTaskName = "createSPMConfigFile"

const val fileDataExtensionName = "spmProperties"

const val spmKindFieldValue = "remoteSourceControl"
const val defaultConfigVersion = 2