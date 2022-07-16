package gradle.multiplatform.spm.services

import gradle.multiplatform.spm.model.git.GitRepositoryData
import gradle.multiplatform.spm.model.serialization.git.GitCommitData
import gradle.multiplatform.spm.model.serialization.git.GitRecordData
import gradle.multiplatform.spm.model.spm.SpmSourceType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

class GitApiService : RepositoryService {
    override fun getRepositoryData(url: String, targetName: String, type: SpmSourceType): GitRepositoryData {
        return getBranchContent(url, targetName)
    }

    private fun getBranchContent(url: String, branchName: String): GitRepositoryData {
        val client = HttpClient.newBuilder().build()
        val jsonWorker = Json { ignoreUnknownKeys = true }

        val components = url.split("/")
        val owner = components[components.size - 2]
        val repo = components[components.size - 1]

        val gitApiCommitPath = "https://api.github.com/repos/$owner/$repo/commits/$branchName"
        val commitRequest = HttpRequest.newBuilder()
            .uri(URI.create(gitApiCommitPath))
            .build()
        val commitResponse = client.send(commitRequest, HttpResponse.BodyHandlers.ofString())
        val parsedCommitContent = jsonWorker.decodeFromString<GitCommitData>(commitResponse.body())

        val gitApiContentPath = "https://api.github.com/repos/$owner/$repo/contents/${PACKAGE_FILE_NAME}?ref=$branchName"
        val request = HttpRequest.newBuilder()
            .uri(URI.create(gitApiContentPath))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val parsedContent = jsonWorker.decodeFromString<GitRecordData>(response.body())

        val decodedContentBytes = Base64.getMimeDecoder().decode(parsedContent.content)
        val decodedContent = String(decodedContentBytes)

        val firstIndex = decodedContent.indexOf('"')
        val secondIndex = decodedContent.indexOf('"', firstIndex + 1)

        val identity = decodedContent.substring(firstIndex + 1, secondIndex)
        return GitRepositoryData(
            packageIdentity =  identity,
            revisionId = parsedCommitContent.sha ?: ""
        )
    }

    companion object {
        private const val PACKAGE_FILE_NAME = "Package.swift"
    }
}