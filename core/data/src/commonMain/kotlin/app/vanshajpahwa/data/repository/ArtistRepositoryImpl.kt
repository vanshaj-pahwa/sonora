package app.vanshajpahwa.data.repository

import app.vanshajpahwa.data.db.datasource.LocalDataSource
import app.vanshajpahwa.data.extension.getFullDataFromDB
import app.vanshajpahwa.data.parser.parseArtistData
import app.vanshajpahwa.domain.data.entities.ArtistEntity
import app.vanshajpahwa.domain.data.model.browse.artist.ArtistBrowse
import app.vanshajpahwa.domain.repository.ArtistRepository
import app.vanshajpahwa.domain.utils.Resource
import app.vanshajpahwa.kotlinytmusicscraper.YouTube
import app.vanshajpahwa.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

internal class ArtistRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val youTube: YouTube,
) : ArtistRepository {
    override fun getAllArtists(limit: Int): Flow<List<ArtistEntity>> =
        flow {
            emit(localDataSource.getAllArtists(limit))
        }.flowOn(Dispatchers.IO)

    override fun getArtistById(id: String): Flow<ArtistEntity?> =
        flow {
            emit(localDataSource.getArtist(id))
        }.flowOn(Dispatchers.IO)

    override fun getArtistByIdOrName(
        id: String?,
        name: String?,
    ): Flow<ArtistEntity?> =
        flow {
            val byId = id?.takeIf { it.isNotBlank() }?.let { localDataSource.getArtist(it) }
            emit(byId ?: name?.takeIf { it.isNotBlank() }?.let { localDataSource.getArtistByName(it) })
        }.flowOn(Dispatchers.IO)

    override suspend fun insertArtist(artistEntity: ArtistEntity) =
        withContext(Dispatchers.IO) {
            localDataSource.insertArtist(artistEntity)
        }

    override suspend fun updateArtistImage(
        channelId: String,
        thumbnail: String,
    ) = withContext(
        Dispatchers.Main,
    ) {
        localDataSource.updateArtistImage(
            channelId,
            thumbnail,
        )
    }

    override suspend fun updateArtistNameLogo(
        channelId: String,
        nameLogoUrl: String?,
        nameLogoColor: String?,
    ) = withContext(Dispatchers.IO) {
        localDataSource.updateArtistNameLogo(channelId, nameLogoUrl, nameLogoColor)
    }

    override suspend fun updateFollowedStatus(
        channelId: String,
        followedStatus: Int,
    ) = withContext(
        Dispatchers.Main,
    ) { localDataSource.updateFollowed(followedStatus, channelId) }

    override fun getFollowedArtists(): Flow<List<ArtistEntity>> =
        flow {
            emit(
                getFullDataFromDB { limit, offset ->
                    localDataSource.getFollowedArtists(limit, offset)
                },
            )
        }.flowOn(Dispatchers.IO)

    override suspend fun updateArtistInLibrary(
        inLibrary: LocalDateTime,
        channelId: String,
    ) = withContext(Dispatchers.Main) {
        localDataSource.updateArtistInLibrary(
            inLibrary,
            channelId,
        )
    }

    override fun getArtistData(channelId: String): Flow<Resource<ArtistBrowse>> =
        flow {
            runCatching {
                youTube
                    .artist(channelId)
                    .onSuccess { result ->
                        emit(Resource.Success<ArtistBrowse>(parseArtistData(result)))
                    }.onFailure { e ->
                        Logger.d("Artist", "Error: ${e.message}")
                        emit(Resource.Error<ArtistBrowse>(e.message.toString()))
                    }
            }
        }.flowOn(Dispatchers.IO)
}