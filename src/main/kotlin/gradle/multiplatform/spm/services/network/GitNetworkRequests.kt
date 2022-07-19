package gradle.multiplatform.spm.services.network

import gradle.multiplatform.spm.model.git.UrlData
import gradle.multiplatform.spm.model.serialization.git.fileContent.GitRecordData
import gradle.multiplatform.spm.model.serialization.git.branchData.GitBranchData
import gradle.multiplatform.spm.model.serialization.git.tagsData.GitTagData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GitNetworkRequests {
    companion object {
        private const val BASE_ADDRESS = "https://api.github.com"
        private const val TIMEOUT_VALUE = 60000L

        private const val BRANCHES_LIST_ERROR = "Get branches list failed with status %d"
        private const val TAGS_LIST_ERROR = "Get tags list failed with status %d"
        private const val FILE_CONTENT_ERROR = "Get file content failed with status %d"
    }

    private val client = HttpClient.newBuilder().build()
    private val jsonWorker = Json { ignoreUnknownKeys = true }

    @Throws(IOException::class)
    fun getBranchesList(urlData: UrlData): List<GitBranchData> {
        val urlString = "$BASE_ADDRESS/repos/${urlData.repoOwner}/${urlData.repoName}/branches"
        val branchesRequest = HttpRequest.newBuilder()
            .uri(URI.create(urlString))
            .build()
        val branchesResponse = client.send(branchesRequest, HttpResponse.BodyHandlers.ofString())

        return when(branchesResponse.statusCode() == 200) {
            true -> jsonWorker.decodeFromString(branchesResponse.body())
            false -> throw IOException(BRANCHES_LIST_ERROR.format(branchesResponse.statusCode()))
        }
    }

    fun getTagsList(
        urlData: UrlData
    ): List<GitTagData> {
        val urlString = "$BASE_ADDRESS/repos/${urlData.repoOwner}/${urlData.repoName}/tags"
        val tagsRequest = HttpRequest.newBuilder()
            .uri(URI.create(urlString))
            .build()
        val tagsResponse = client.send(tagsRequest, HttpResponse.BodyHandlers.ofString())
        return when(tagsResponse.statusCode() == 200) {
            true -> jsonWorker.decodeFromString(tagsResponse.body())
            false -> throw IOException(TAGS_LIST_ERROR.format(tagsResponse.statusCode()))
        }
    }

    fun getFileContentFromSource(
        urlData: UrlData,
        sourceName: String,
        fileName: String
    ): GitRecordData {
        val urlString = "$BASE_ADDRESS/repos/${urlData.repoOwner}/${urlData.repoName}/contents$fileName?ref=$sourceName"
        val contentRequest = HttpRequest.newBuilder()
            .uri(URI.create(urlString))
            .build()
        val contentResponse = client.send(contentRequest, HttpResponse.BodyHandlers.ofString())

        return when(contentResponse.statusCode() == 200) {
            true -> jsonWorker.decodeFromString(contentResponse.body())
            false -> throw IOException(FILE_CONTENT_ERROR.format(contentResponse.statusCode()))
        }
    }
}