package gradle.multiplatform.spm.services

import com.github.syari.kgit.KGit
import gradle.multiplatform.spm.model.errorDuringGitParsing
import gradle.multiplatform.spm.model.git.FailedGitDataException
import gradle.multiplatform.spm.model.git.GitRepositoryData
import gradle.multiplatform.spm.model.serialization.git.GitRecordData
import gradle.multiplatform.spm.model.serialization.spm.MainFileContent
import gradle.multiplatform.spm.model.spm.SpmSourceType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.TextProgressMonitor
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

class KGitService {
    private val cloneFolder = File(CLONED_FOLDER_NAME)

    init {
        if (!cloneFolder.exists()) {
            cloneFolder.mkdir()
        }
    }

    fun getRepositoryData(url: String, targetName: String, type: SpmSourceType): GitRepositoryData {
        clearCloneFolder()

        return try {
            when(type) {
                SpmSourceType.Branch -> getBranchContent(url = url, branchName = targetName)
                SpmSourceType.CurrentVersion -> getCurrentVersionContent(url = url, version = targetName)
            }
        } catch (anyException: Exception) {
            throw FailedGitDataException("$errorDuringGitParsing $url.", anyException)
        } finally {
            clearCloneFolder()
        }
    }

    private fun getCurrentVersionContent(url: String, version: String): GitRepositoryData {
        val clonedRepository = KGit.cloneRepository {
            setURI(url)
            setTimeout(TIME_OUT_VALUE)
            setProgressMonitor(TextProgressMonitor())
            setDirectory(cloneFolder)
        }

        val tags = clonedRepository.repository.refDatabase.getRefsByPrefix(Constants.R_TAGS)
        val found = tags.first { it.name.contains(version) }

        val data = getDataFromCommit(clonedRepository.repository, found.objectId)
        clonedRepository.close()

        return data
    }

    private fun getBranchContent(url: String, branchName: String): GitRepositoryData {
        val components = url.split("/")
        val owner = components[components.size - 2]
        val repo = components[components.size - 1]

        val gitApiPath = "https://api.github.com/repos/$owner/$repo/contents/$PACKAGE_FILE_NAME"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(gitApiPath))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response.body())

        val jsonWorker = Json {
            ignoreUnknownKeys = true
        }


        val dd = jsonWorker.decodeFromString<GitRecordData>(response.body())
        //val ddd = dd.content!!.replace("\n", "")

        val enc = Base64.getMimeDecoder().decode(dd.content)
        val sss = String(enc)

        println(sss)





        println(components[components.size - 1])
        println(components[components.size - 2])


        val clonedRepository = KGit.cloneRepository {
            setURI(url)
            setTimeout(TIME_OUT_VALUE)
            setProgressMonitor(TextProgressMonitor())
            setBranch(branchName)
            setDirectory(cloneFolder)
        }

        val commitObject = clonedRepository.repository.resolve(Constants.HEAD)
        val data = getDataFromCommit(clonedRepository.repository, commitObject)

        clonedRepository.close()

        return data
    }

    private fun getDataFromCommit(repository: Repository, commitObject: ObjectId): GitRepositoryData {
        val revWalk = RevWalk(repository)

        val commit = revWalk.parseCommit(commitObject)
        val commitTree = commit.tree

        val commitWalk = TreeWalk.forPath(repository, PACKAGE_FILE_NAME, commitTree)
        val objectId = commitWalk.getObjectId(0)
        val loader = repository.open(objectId)
        val fileContent = String(loader.bytes)

        val firstIndex = fileContent.indexOf('"')
        val secondIndex = fileContent.indexOf('"', firstIndex + 1)

        val identity = fileContent.substring(firstIndex + 1, secondIndex)
        val data = GitRepositoryData(
            packageIdentity =  identity,
            revisionId = commitObject.name
        )
        revWalk.dispose()

        return data
    }

    private fun clearCloneFolder() {
        val files = cloneFolder.listFiles()
        files.indices.forEach { index ->
            files[index].deleteRecursively()
        }
    }

    companion object {
        private const val TIME_OUT_VALUE: Int = 10
        private const val PACKAGE_FILE_NAME = "Package.swift"
        private const val CLONED_FOLDER_NAME = "clonedRepositories"
    }
}