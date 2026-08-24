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
     * Update checking is switched off, at the repository rather than the call sites.
     *
     * Both checks pointed at somebody else's project: the GitHub one asked
     * maxrave-dev/Sonora for its latest release, and the F-Droid one asked for a package that
     * was never published there. Neither could ever return an update for this app, and the GitHub
     * one would have offered a build with a different applicationId, which installs alongside this
     * one rather than updating it.
     *
     * Disabled here so MainActivity, SharedViewModel and the Settings row keep working untouched:
     * they simply always hear "no update". Restoring it later means putting a real releases source
     * back in these two functions and nothing else.
     */
    override fun checkForGithubReleaseUpdate(): Flow<Resource<UpdateData>> =
        flow<Resource<UpdateData>> {
            emit(Resource.Error("Update checking is disabled"))
        }.flowOn(Dispatchers.IO)

    override fun checkForFdroidUpdate(): Flow<Resource<UpdateData>> =
        flow<Resource<UpdateData>> {
            emit(Resource.Error("Update checking is disabled"))
        }.flowOn(Dispatchers.IO)

}