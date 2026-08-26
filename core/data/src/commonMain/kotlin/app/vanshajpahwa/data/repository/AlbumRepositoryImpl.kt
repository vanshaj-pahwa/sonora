package app.vanshajpahwa.data.repository

import app.vanshajpahwa.data.db.datasource.LocalDataSource
import app.vanshajpahwa.data.extension.getFullDataFromDB
import app.vanshajpahwa.data.mapping.toAlbumsResult
import app.vanshajpahwa.data.parser.parseAlbumData
import app.vanshajpahwa.domain.data.entities.AlbumEntity
import app.vanshajpahwa.domain.data.entities.FollowedArtistSingleAndAlbum
import app.vanshajpahwa.domain.data.model.browse.album.AlbumBrowse
import app.vanshajpahwa.domain.data.model.searchResult.albums.AlbumsResult
import app.vanshajpahwa.domain.data.model.searchResult.songs.Artist
import app.vanshajpahwa.domain.data.model.searchResult.songs.Thumbnail
import app.vanshajpahwa.domain.repository.AlbumRepository
import app.vanshajpahwa.domain.utils.Resource
import app.vanshajpahwa.domain.utils.toTrack
import app.vanshajpahwa.kotlinytmusicscraper.YouTube
import app.vanshajpahwa.kotlinytmusicscraper.models.AlbumItem
import app.vanshajpahwa.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

private const val TAG = "AlbumRepositoryImpl"

internal class AlbumRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val youTube: YouTube,
) : AlbumRepository {
    override fun getAllAlbums(limit: Int): Flow<List<AlbumEntity>> =
        flow {
            emit(localDataSource.getAllAlbums(limit))
        }.flowOn(Dispatchers.IO)

    override fun getAlbum(id: String): Flow<AlbumEntity?> =
        flow {
            emit(localDataSource.getAlbum(id))
        }.flowOn(Dispatchers.IO)

    override fun getAlbumAsFlow(id: String) = localDataSource.getAlbumAsFlow(id)

    override fun getLikedAlbums(): Flow<List<AlbumEntity>> =
        localDataSource.getLikedAlbumsAsFlow().flowOn(Dispatchers.IO)

    override fun insertAlbum(albumEntity: AlbumEntity) =
        flow {
            emit(localDataSource.insertAlbum(albumEntity))
        }.flowOn(Dispatchers.IO)

    override suspend fun updateAlbumLiked(
        albumId: String,
        likeStatus: Int,
    ) = withContext(Dispatchers.Main) { localDataSource.updateAlbumLiked(likeStatus, albumId) }

    override suspend fun updateAlbumInLibrary(
        inLibrary: LocalDateTime,
        albumId: String,
    ) = withContext(
        Dispatchers.Main,
    ) { localDataSource.updateAlbumInLibrary(inLibrary, albumId) }

    override suspend fun updateAlbumDownloadState(
        albumId: String,
        downloadState: Int,
    ) = withContext(Dispatchers.Main) {
        localDataSource.updateAlbumDownloadState(
            downloadState,
            albumId,
        )
    }

    override suspend fun insertFollowedArtistSingleAndAlbum(followedArtistSingleAndAlbum: FollowedArtistSingleAndAlbum) =
        withContext(Dispatchers.IO) {
            localDataSource.insertFollowedArtistSingleAndAlbum(followedArtistSingleAndAlbum)
        }

    override suspend fun deleteFollowedArtistSingleAndAlbum(channelId: String) =
        withContext(Dispatchers.IO) {
            localDataSource.deleteFollowedArtistSingleAndAlbum(channelId)
        }

    override suspend fun getAllFollowedArtistSingleAndAlbums(): Flow<List<FollowedArtistSingleAndAlbum>?> =
        flow {
            val list =
                getFullDataFromDB { limit, offset ->
                    localDataSource.getAllFollowedArtistSingleAndAlbums(limit, offset)
                }
            emit(list)
        }.flowOn(Dispatchers.IO)

    override suspend fun getFollowedArtistSingleAndAlbum(channelId: String): Flow<FollowedArtistSingleAndAlbum?> =
        flow {
            emit(localDataSource.getFollowedArtistSingleAndAlbum(channelId))
        }.flowOn(Dispatchers.IO)

    override fun getAlbumData(browseId: String): Flow<Resource<AlbumBrowse>> =
        flow {
            runCatching {
                youTube
                    .album(browseId, withSongs = true)
                    .onSuccess { result ->
                        emit(Resource.Success(parseAlbumData(result)))
                    }.onFailure { e ->
                        Logger.d(TAG, "getAlbumData -> error: ${e.message}")
                        // Albums are written to the album table when opened, so fall back to that
                        // copy before reporting a failure. Without it an album browsed earlier shows
                        // an error offline, even when its audio is downloaded.
                        val cached = cachedAlbum(browseId)
                        if (cached != null) {
                            Logger.d(TAG, "getAlbumData -> served $browseId from cache")
                            emit(Resource.Success(cached))
                        } else {
                            emit(Resource.Error(e.message.toString()))
                        }
                    }
            }
        }.flowOn(Dispatchers.IO)

    /**
     * Rebuilds an album from what the device already stored, or null if too little was kept.
     *
     * Null rather than a header with no tracks under it, which reads as a load that never finishes.
     * Order comes from the album's own track list because `WHERE videoId IN (...)` does not preserve
     * it, and running order is part of what an album is.
     */
    private suspend fun cachedAlbum(browseId: String): AlbumBrowse? {
        val album = localDataSource.getAlbum(browseId) ?: return null
        val trackIds = album.tracks.orEmpty()
        if (trackIds.isEmpty()) return null
        val byId =
            localDataSource
                .getSongByListVideoIdFull(trackIds)
                .associateBy { it.videoId }
        val tracks = trackIds.mapNotNull { byId[it]?.toTrack() }
        if (tracks.isEmpty()) return null
        return AlbumBrowse(
            artists =
                album.artistName.orEmpty().mapIndexed { index, name ->
                    Artist(id = album.artistId?.getOrNull(index), name = name)
                },
            audioPlaylistId = album.audioPlaylistId,
            description = album.description,
            duration = album.duration,
            durationSeconds = album.durationSeconds,
            // Stored as a single url with no known size. Callers take the last entry, and one
            // entry is both first and last.
            thumbnails = album.thumbnails?.let { listOf(Thumbnail(height = 544, url = it, width = 544)) },
            title = album.title,
            trackCount = album.trackCount,
            tracks = tracks,
            type = album.type,
            year = album.year,
            // Not stored. The page hides the row when it is empty.
            otherVersion = emptyList(),
        )
    }

    override fun getAlbumMore(
        browseId: String,
        params: String,
    ): Flow<Pair<String, List<AlbumsResult>>?> =
        flow {
            runCatching {
                youTube
                    .browse(browseId = browseId, params = params)
                    .onSuccess { data ->
                        Logger.w(TAG, "getAlbumMore -> result: $data")
                        val items =
                            (data.items.firstOrNull()?.items ?: emptyList()).mapNotNull { item ->
                                item as? AlbumItem
                            }
                        emit(
                            (data.title ?: "") to (
                                items.map {
                                    it.toAlbumsResult()
                                }
                            ),
                        )
                    }.onFailure {
                        it.printStackTrace()
                        emit(null)
                    }
            }
        }.flowOn(Dispatchers.IO)
}