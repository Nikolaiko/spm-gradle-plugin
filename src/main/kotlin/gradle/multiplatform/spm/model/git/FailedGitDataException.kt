package gradle.multiplatform.spm.model.git

class FailedGitDataException(
    errorMessage: String,
    cause: Throwable
) : Exception(errorMessage, cause)