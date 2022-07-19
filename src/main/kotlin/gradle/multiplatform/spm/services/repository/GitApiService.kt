package gradle.multiplatform.spm.services.repository

import gradle.multiplatform.spm.model.errorDuringGitParsing
import gradle.multiplatform.spm.model.git.FailedGitDataException
import gradle.multiplatform.spm.model.git.GitRepositoryData
import gradle.multiplatform.spm.model.git.UrlData
import gradle.multiplatform.spm.model.serialization.git.fileContent.GitRecordData
import gradle.multiplatform.spm.model.serialization.git.branchData.GitBranchData
import gradle.multiplatform.spm.model.serialization.git.tagsData.GitTagData
import gradle.multiplatform.spm.model.spm.SpmSourceType
import gradle.multiplatform.spm.services.network.GitNetworkRequests
import gradle.multiplatform.spm.services.parsers.PackageFileParser
import java.io.IOException
import java.util.*
import kotlin.NoSuchElementException

class GitApiService {
    private val network = GitNetworkRequests()
    private val parser = PackageFileParser()

    @Throws(FailedGitDataException::class)
    fun getRepositoryData(url: String, targetName: String, type: SpmSourceType): GitRepositoryData {
        return when(type) {
            SpmSourceType.Branch -> getBranchContent(url, targetName)
            SpmSourceType.CurrentVersion -> getReleaseContent(url, targetName)
        }
    }

    private fun getReleaseContent(url: String, version: String): GitRepositoryData {
        val urlData = parseUrl(url)
        var releaseName = version

        val tagData = when(val foundTag = getTagData(urlData, releaseName)) {
            null -> {
                releaseName = "v$version"
                getTagData(urlData, releaseName)
            }
            else -> foundTag
        } ?: throw buildException("No tag with name $version", NoSuchElementException())


        val recordContent = getFileContentFromSource(urlData, releaseName)
        val decoded = decodeContent(recordContent.content ?: "")

        val identity = parser.parseFileContent(decoded)

        return GitRepositoryData(
            packageIdentity = identity,
            revisionId = tagData.commit.sha
        )
    }

    @Throws(FailedGitDataException::class)
    private fun getBranchContent(url: String, branchName: String): GitRepositoryData {
        val urlData = parseUrl(url)

        val branch = getBranchData(urlData, branchName)
        val recordContent = getFileContentFromSource(urlData, branchName)
        val decoded = decodeContent(recordContent.content ?: "")

        val firstIndex = decoded.indexOf('"')
        val secondIndex = decoded.indexOf('"', firstIndex + 1)
        val identity = decoded.substring(firstIndex + 1, secondIndex)


        return GitRepositoryData(
            packageIdentity = identity,
            revisionId = branch.commit.sha
        )
//        val gitApiCommitPath = "https://api.github.com/repos/${urlData.repoOwner}/${urlData.repoName}/commits/$branchName"
//        val commitRequest = HttpRequest.newBuilder()
//            .uri(URI.create(gitApiCommitPath))
//            .build()
//        val commitResponse = client.send(commitRequest, HttpResponse.BodyHandlers.ofString())
//        val parsedCommitContent = jsonWorker.decodeFromString<GitCommitData>(commitResponse.body())
//
//        val gitApiContentPath = "https://api.github.com/repos/${urlData.repoOwner}/${urlData.repoName}/contents/${PACKAGE_FILE_NAME}?ref=$branchName"
//        val request = HttpRequest.newBuilder()
//            .uri(URI.create(gitApiContentPath))
//            .build()
//        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
//        val parsedContent = jsonWorker.decodeFromString<GitRecordData>(response.body())
//
//        val decodedContentBytes = Base64.getMimeDecoder().decode(parsedContent.content)
//        val decodedContent = String(decodedContentBytes)
//
//        val firstIndex = decodedContent.indexOf('"')
//        val secondIndex = decodedContent.indexOf('"', firstIndex + 1)
//
//        val identity = decodedContent.substring(firstIndex + 1, secondIndex)
//        return GitRepositoryData(
//            packageIdentity =  identity,
//            revisionId = parsedCommitContent.sha ?: ""
//        )
    }

    @Throws(FailedGitDataException::class)
    private fun getBranchData(urlData: UrlData, branchName: String): GitBranchData {
        val branches = try {
            network.getBranchesList(urlData)
        } catch (requestError: IOException) {
            throw buildException(requestError.message, requestError)
        }
        return try {
            branches.first { it.name == branchName }
        } catch (notFoundError: NoSuchElementException) {
            throw buildException(notFoundError.message, notFoundError)
        }
    }

    @Throws(FailedGitDataException::class)
    private fun getTagData(urlData: UrlData, tagName: String): GitTagData? {
        val tags = try {
            network.getTagsList(urlData)
        } catch (requestError: IOException) {
            throw buildException(requestError.message, requestError)
        }
        return tags.firstOrNull { it.name == tagName }
    }

    @Throws(FailedGitDataException::class)
    private fun getFileContentFromSource(
        urlData: UrlData,
        branchName: String
    ): GitRecordData = try {
        network.getFileContentFromSource(urlData, branchName, PACKAGE_FILE_NAME)
    } catch (requestError: IOException) {
        throw buildException(requestError.message, requestError)
    }

    @Throws(FailedGitDataException::class)
    private fun parseUrl(url: String): UrlData {
        val components = url.split("/")
        return try {
            UrlData(
                repoOwner = components[components.size - 2],
                repoName = components[components.size - 1]
            )
        } catch (notFound: IndexOutOfBoundsException) {
            throw buildException(notFound.message, notFound)
        }
    }

    private fun buildException(message: String?, cause: Exception) =
        FailedGitDataException(
            errorMessage = message ?: errorDuringGitParsing,
            cause = cause
        )

    private fun decodeContent(content: String): String {
        val decodedContentBytes = Base64.getMimeDecoder().decode(content)
        return String(decodedContentBytes)
    }

    companion object {
        private const val PACKAGE_FILE_NAME = "Package.swift"
    }
}