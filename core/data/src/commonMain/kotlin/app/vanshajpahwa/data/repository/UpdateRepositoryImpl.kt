package app.vanshajpahwa.data.repository

import app.vanshajpahwa.domain.data.model.update.UpdateData
import app.vanshajpahwa.domain.repository.UpdateRepository
import app.vanshajpahwa.domain.utils.Resource
import app.vanshajpahwa.kotlinytmusicscraper.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class UpdateRepositoryImpl(
    private val youTube: YouTube,
) : UpdateRepository {
    /**
     * The latest release of this project, or an error.
     *
     * This asks GitHub for the newest tag and hands back what it says. Deciding whether that tag is
     * newer than the running build belongs to the caller, which is the only place the running
     * version is known.
     */
    override fun checkForGithubReleaseUpdate(): Flow<Resource<UpdateData>> =
        flow {
            youTube
                .checkForGithubReleaseUpdate()
                .onSuccess { response ->
                    val tag = response.tagName
                    if (tag.isNullOrBlank()) {
                        emit(Resource.Error("No release found"))
                    } else {
                        emit(
                            Resource.Success(
                                UpdateData(
                                    tagName = tag,
                                    releaseTime = response.publishedAt,
                                    body = response.body.orEmpty(),
                                ),
                            ),
                        )
                    }
                }.onFailure {
                    emit(Resource.Error(it.message ?: "Could not reach GitHub"))
                }
        }.flowOn(Dispatchers.IO)

    /**
     * Still off. The package was never published to F-Droid, so this can only ever fail.
     */
    override fun checkForFdroidUpdate(): Flow<Resource<UpdateData>> =
        flow<Resource<UpdateData>> {
            emit(Resource.Error("Not published on F-Droid"))
        }.flowOn(Dispatchers.IO)
}
