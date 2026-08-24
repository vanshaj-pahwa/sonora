@file:Suppress("ktlint:standard:no-wildcard-imports")

package app.vanshajpahwa.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.vanshajpahwa.data.db.Converters
import app.vanshajpahwa.data.db.datasource.LocalDataSource
import app.vanshajpahwa.data.extension.getFullDataFromDB
import app.vanshajpahwa.data.mapping.toListTrack
import app.vanshajpahwa.data.mapping.toTrack
import app.vanshajpahwa.data.paging.LocalPlaylistPagingSource
import app.vanshajpahwa.data.paging.LocalPlaylistTimeBasedPagingSource
import app.vanshajpahwa.data.parser.parseSetVideoId
import app.vanshajpahwa.domain.data.entities.DownloadState
import app.vanshajpahwa.domain.data.entities.LocalPlaylistEntity
import app.vanshajpahwa.domain.data.entities.LocalPlaylistEntity.YouTubeSyncState.Synced
import app.vanshajpahwa.domain.data.entities.LocalPlaylistEntity.YouTubeSyncState.Syncing
import app.vanshajpahwa.domain.data.entities.PairSongLocalPlaylist
import app.vanshajpahwa.domain.data.entities.SetVideoIdEntity
import app.vanshajpahwa.domain.data.entities.SongEntity
import app.vanshajpahwa.domain.data.model.browse.album.Track
import app.vanshajpahwa.domain.data.model.browse.playlist.PlaylistState
import app.vanshajpahwa.domain.extension.now
import app.vanshajpahwa.domain.repository.LocalPlaylistRepository
import app.vanshajpahwa.domain.utils.FilterState
import app.vanshajpahwa.domain.utils.LocalResource
import app.vanshajpahwa.domain.utils.toListVideoId
import app.vanshajpahwa.domain.utils.toSongEntity
import app.vanshajpahwa.domain.utils.wrapDataResource
import app.vanshajpahwa.domain.utils.wrapMessageResource
import app.vanshajpahwa.kotlinytmusicscraper.YouTube
import app.vanshajpahwa.kotlinytmusicscraper.extension.verifyYouTubePlaylistId
import app.vanshajpahwa.kotlinytmusicscraper.models.MusicShelfRenderer
import app.vanshajpahwa.kotlinytmusicscraper.models.SongItem
import app.vanshajpahwa.kotlinytmusicscraper.models.response.SearchResponse
import app.vanshajpahwa.kotlinytmusicscraper.pages.NextPage
import app.vanshajpahwa.kotlinytmusicscraper.pages.SearchPage
import app.vanshajpahwa.kotlinytmusicscraper.parser.getPlaylistContinuation
import app.vanshajpahwa.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

private const val TAG = "LocalPlaylistRepositoryImpl"

internal class LocalPlaylistRepositoryImpl(
    internal val localDataSource: LocalDataSource,
    internal val youTube: YouTube,
): LocalPlaylistRepository {
    override fun getLocalPlaylist(id: Long) =
        wrapDataResource {
            localDataSource.getLocalPlaylist(id)
        }

    override fun getAllLocalPlaylists(): Flow<List<LocalPlaylistEntity>> =
        flow {
            val list =
                getFullDataFromDB { limit, offset ->
                    localDataSource.getAllLocalPlaylists(limit, offset)
                }
            emit(list)
        }.flowOn(Dispatchers.IO)

    override suspend fun updateLocalPlaylistTracks(
        tracks: List<String>,
        id: Long,
    ) = withContext(Dispatchers.IO) { localDataSource.updateLocalPlaylistTracks(tracks, id) }

    override suspend fun updateLocalPlaylistDownloadState(
        downloadState: Int,
        id: Long,
    ) = withContext(Dispatchers.IO) {
        localDataSource.updateLocalPlaylistDownloadState(
            downloadState,
            id,
        )
    }

    override suspend fun updateLocalPlaylistYouTubePlaylistSyncState(
        id: Long,
        syncState: Int,
    ) = withContext(Dispatchers.IO) {
        localDataSource.updateLocalPlaylistYouTubePlaylistSyncState(id, syncState)
    }

    override fun downloadStateFlow(id: Long): Flow<Int> = localDataSource.getDownloadStateFlowOfLocalPlaylist(id)

    override fun getAllDownloadingLocalPlaylists(): Flow<List<LocalPlaylistEntity>> =
        flow {
            emit(
                getFullDataFromDB { limit, offset ->
                    localDataSource.getAllDownloadingLocalPlaylists(limit, offset)
                },
            )
        }.flowOn(Dispatchers.IO)

    override fun listTrackFlow(id: Long): Flow<List<String>> =
        localDataSource
            .getListTracksFlowOfLocalPlaylist(id)
            .map { Converters().fromString(it.firstOrNull()) ?: emptyList() }

    override fun getTracksPaging(
        id: Long,
        filter: FilterState,
    ): Flow<PagingData<Pair<SongEntity, PairSongLocalPlaylist>>> {
        if (filter == FilterState.CustomOrder || filter == FilterState.Title) {
            return Pager(
                config = PagingConfig(pageSize = 100, prefetchDistance = 5),
                pagingSourceFactory = {
                    LocalPlaylistPagingSource(
                        playlistId = id,
                        filter = filter,
                        localDataSource = localDataSource,
                    )
                },
            ).flow
        } else {
            return Pager(
                config = PagingConfig(pageSize = 100, prefetchDistance = 5),
                pagingSourceFactory = {
                    LocalPlaylistTimeBasedPagingSource(
                        playlistId = id,
                        filter = filter,
                        localDataSource = localDataSource,
                    )
                },
            ).flow
        }
    }

    override suspend fun getFullPlaylistTracks(id: Long): List<SongEntity> {
        val playlist = localDataSource.getLocalPlaylist(id) ?: return emptyList()
        Logger.d(TAG, "getFullPlaylistTracks: $playlist")
        val tracks = mutableListOf<SongEntity>()
        var currentPage = 0
        while (true) {
            val pairs =
                localDataSource.getPlaylistPairSongByOffset(
                    playlistId = id,
                    filterState = FilterState.CustomOrder,
                    offset = currentPage,
                )
            if (pairs.isNullOrEmpty()) {
                break
            }
            val songs =
                localDataSource
                    .getSongByListVideoIdFull(
                        pairs.map { it.songId },
                    )
            val idValue = songs.associateBy { it.videoId }
            val sorted =
                pairs.mapNotNull {
                    idValue[it.songId]
                }
            tracks.addAll(sorted)
            currentPage++
        }
        return tracks
    }

    override suspend fun getListTrackVideoId(id: Long): List<String> {
        val playlist = localDataSource.getLocalPlaylist(id)
        return playlist?.tracks ?: emptyList()
    }

    override fun insertLocalPlaylist(
        localPlaylist: LocalPlaylistEntity,
        successMessage: String,
    ): Flow<LocalResource<String>> =
        wrapMessageResource(
            successMessage = successMessage,
        ) {
            localDataSource.insertLocalPlaylist(localPlaylist)
        }

    override fun deleteLocalPlaylist(
        id: Long,
        successMessage: String,
    ) = wrapMessageResource(
        successMessage = successMessage,
    ) {
        localDataSource.deleteLocalPlaylist(id)
    }

    override fun updateTitleLocalPlaylist(
        id: Long,
        newTitle: String,
        updatedMessage: String,
        updatedYtMessage: String,
        errorMessage: String,
    ): Flow<LocalResource<String>> =
        flow {
            emit(LocalResource.Loading<String>())
            runCatching {
                localDataSource.updateLocalPlaylistTitle(id = id, title = newTitle)
            }.onSuccess {
                emit(LocalResource.Success(updatedMessage))
                val localPlaylist = localDataSource.getLocalPlaylist(id)
                val ytId = localPlaylist?.youtubePlaylistId
                if (ytId != null) {
                    youTube
                        .editPlaylist(ytId, newTitle)
                        .onSuccess {
                            emit(LocalResource.Success(updatedYtMessage))
                        }.onFailure {
                            emit(LocalResource.Error<String>(it.message ?: errorMessage))
                        }
                }
            }.onFailure {
                emit(LocalResource.Error<String>(it.message ?: errorMessage))
            }
        }.flowOn(Dispatchers.IO)

    override fun updateThumbnailLocalPlaylist(
        id: Long,
        newThumbnail: String,
        successMessage: String,
    ) = wrapMessageResource(
        successMessage = successMessage,
    ) {
        localDataSource.updateLocalPlaylistThumbnail(id = id, thumbnail = newThumbnail)
    }

    override fun updateDownloadState(
        id: Long,
        downloadState: Int,
        successMessage: String,
    ) = wrapMessageResource(
        successMessage = successMessage,
    ) {
        localDataSource.updateLocalPlaylistDownloadState(id = id, downloadState = downloadState)
    }

    override fun syncYouTubePlaylistToLocalPlaylist(
        playlist: PlaylistState,
        tracks: List<Track>,
        successMessage: String,
        errorMessage: String,
    ): Flow<LocalResource<String>> =
        syncYouTubePlaylistToLocalPlaylistImpl(playlist, tracks, successMessage, errorMessage)

    override fun syncLocalPlaylistToYouTubePlaylist(
        playlistId: Long,
        successMessage: String,
        errorMessage: String,
    ) =
        syncLocalPlaylistToYouTubePlaylistImpl(playlistId, successMessage, errorMessage)

    override fun unsyncLocalPlaylist(
        id: Long,
        successMessage: String,
    ) =
        unsyncLocalPlaylistImpl(id, successMessage)

    override fun updateSyncState(
        id: Long,
        syncState: Int,
        successMessage: String,
    ) =
        updateSyncStateImpl(id, syncState, successMessage)

    override fun updateYouTubePlaylistId(
        id: Long,
        youtubePlaylistId: String,
        successMessage: String,
    ) =
        updateYouTubePlaylistIdImpl(id, youtubePlaylistId, successMessage)

    override fun updateListTrackSynced(id: Long) =
        updateListTrackSyncedImpl(id)

    override fun addTrackToLocalPlaylist(
        id: Long,
        song: SongEntity,
        successMessage: String,
        updatedYtMessage: String,
        errorMessage: String,
    ): Flow<LocalResource<String>> =
        flow {
            emit(LocalResource.Loading())
            val checkSong = localDataSource.getSong(song.videoId)
            if (checkSong == null) {
                localDataSource.insertSong(song)
            }
            val localPlaylist = localDataSource.getLocalPlaylist(id) ?: return@flow
            val nextPosition = localPlaylist.tracks?.size ?: 0
            val nextPair =
                PairSongLocalPlaylist(
                    playlistId = id,
                    songId = song.videoId,
                    position = nextPosition,
                    inPlaylist = now(),
                )
            runBlocking {
                localDataSource.insertPairSongLocalPlaylist(nextPair)
                localDataSource.updateLocalPlaylistTracks(
                    localPlaylist.tracks?.plus(song.videoId) ?: mutableListOf(song.videoId),
                    id,
                )
            }
            // Emit success message
            emit(LocalResource.Success(successMessage))

            // Add to YouTube playlist
            val ytId = localPlaylist.youtubePlaylistId
            if (ytId != null) {
                youTube
                    .addPlaylistItem(ytId, song.videoId)
                    .onSuccess {
                        val data = it.playlistEditResults
                        if (data.isNotEmpty()) {
                            for (d in data) {
                                localDataSource.insertSetVideoId(
                                    SetVideoIdEntity(
                                        d.playlistEditVideoAddedResultData.videoId,
                                        d.playlistEditVideoAddedResultData.setVideoId,
                                    ),
                                )
                            }
                            emit(LocalResource.Success(updatedYtMessage))
                        } else {
                            emit(LocalResource.Error<String>("$errorMessage: Empty playlistEditResults"))
                        }
                    }.onFailure {
                        emit(LocalResource.Error<String>("$errorMessage: ${it.message}"))
                    }
            }
        }.flowOn(Dispatchers.IO)

    override fun removeTrackFromLocalPlaylist(
        id: Long,
        song: SongEntity,
        successMessage: String,
        updatedYtMessage: String,
        errorMessage: String,
    ): Flow<LocalResource<String>> =
        flow {
            emit(LocalResource.Loading())
            val localPlaylist = localDataSource.getLocalPlaylist(id) ?: return@flow
            val nextTracks = localPlaylist.tracks?.toMutableList() ?: mutableListOf()
            nextTracks.remove(song.videoId)
            localDataSource.updateLocalPlaylistTracks(nextTracks, id)
            localDataSource.deletePairSongLocalPlaylist(id, song.videoId)
            emit(LocalResource.Success(successMessage))
            val ytPlaylistId = localPlaylist.youtubePlaylistId
            if (ytPlaylistId != null) {
                val setVideoId = localDataSource.getSetVideoId(song.videoId)?.setVideoId
                if (setVideoId != null) {
                    youTube
                        .removeItemYouTubePlaylist(ytPlaylistId, song.videoId, setVideoId)
                        .onSuccess {
                            emit(LocalResource.Success(successMessage))
                        }.onFailure {
                            emit(LocalResource.Error<String>("$errorMessage: ${it.message}"))
                        }
                } else {
                    emit(LocalResource.Error<String>("$errorMessage: SetVideoId is null"))
                }
            }
        }.flowOn(Dispatchers.IO)

    override fun getSuggestionsTrackForPlaylist(id: Long): Flow<LocalResource<Pair<String?, List<Track>>>> =
        flow {
            val localPlaylist = localDataSource.getLocalPlaylist(id) ?: return@flow
            val ytPlaylistId = localPlaylist.youtubePlaylistId ?: return@flow

            youTube
                .getSuggestionsTrackForPlaylist(ytPlaylistId)
                .onSuccess { data ->
                    val listSongItem = data?.second?.map { it.toTrack() }
                    if (data != null && !listSongItem.isNullOrEmpty()) {
                        emit(
                            LocalResource.Success(
                                Pair(
                                    data.first,
                                    listSongItem,
                                ),
                            ),
                        )
                    } else {
                        emit(LocalResource.Error("List suggestions is null"))
                    }
                }.onFailure { e ->
                    e.printStackTrace()
                    emit(LocalResource.Error(e.message ?: "Error"))
                }
        }

    override fun reloadSuggestionPlaylist(reloadParams: String): Flow<LocalResource<Pair<String?, List<Track>>>> =
        flow {
            runCatching {
                emit(LocalResource.Loading())
                youTube
                    .customQuery(browseId = "", continuation = reloadParams, setLogin = true)
                    .onSuccess { values ->
                        val data = values.continuationContents?.musicShelfContinuation?.contents
                        val dataResult:
                            ArrayList<SearchResponse.ContinuationContents.MusicShelfContinuation.Content> =
                            arrayListOf()
                        if (!data.isNullOrEmpty()) {
                            dataResult.addAll(data)
                        }
                        val reloadParamsNew =
                            values.continuationContents
                                ?.musicShelfContinuation
                                ?.continuations
                                ?.get(
                                    0,
                                )?.reloadContinuationData
                                ?.continuation
                        if (dataResult.isNotEmpty()) {
                            val listTrack: ArrayList<Track> = arrayListOf()
                            dataResult.forEach {
                                listTrack.add(
                                    (
                                        SearchPage.toYTItem(
                                            it.musicResponsiveListItemRenderer,
                                        ) as SongItem
                                    ).toTrack(),
                                )
                            }
                            emit(LocalResource.Success(Pair(reloadParamsNew, listTrack.toList())))
                        } else {
                            emit(LocalResource.Error("Empty data"))
                        }
                    }.onFailure { exception ->
                        exception.printStackTrace()
                        emit(LocalResource.Error(exception.message ?: "Error"))
                    }
            }
        }.flowOn(Dispatchers.IO)

    override fun getYouTubeSetVideoId(youtubePlaylistId: String): Flow<List<SetVideoIdEntity>> =
        getYouTubeSetVideoIdImpl(youtubePlaylistId)

    override fun addYouTubePlaylistItem(
        youtubePlaylistId: String,
        videoId: String,
    ): Flow<LocalResource<String>> =
        addYouTubePlaylistItemImpl(youtubePlaylistId, videoId)

    override suspend fun insertPairSongLocalPlaylist(pairSongLocalPlaylist: PairSongLocalPlaylist) =
        withContext(Dispatchers.IO) {
            localDataSource.insertPairSongLocalPlaylist(pairSongLocalPlaylist)
        }

    override fun getPlaylistPairSongByListPosition(
        playlistId: Long,
        listPosition: List<Int>,
    ): Flow<List<PairSongLocalPlaylist>?> =
        flow {
            emit(localDataSource.getPlaylistPairSongByListPosition(playlistId, listPosition))
        }.flowOn(Dispatchers.IO)

    override fun getPlaylistPairSongByOffset(
        playlistId: Long,
        offset: Int,
        filterState: FilterState,
    ): Flow<List<PairSongLocalPlaylist>?> =
        flow {
            emit(localDataSource.getPlaylistPairSongByOffset(playlistId, offset, filterState))
        }.flowOn(Dispatchers.IO)

    override fun getPlaylistPairSongByTime(
        playlistId: Long,
        filterState: FilterState,
        localDateTime: LocalDateTime,
    ): Flow<List<PairSongLocalPlaylist>?> =
        flow {
            emit(
                localDataSource.getPlaylistPairSongByTime(
                    playlistId,
                    filterState,
                    localDateTime,
                ),
            )
        }.flowOn(Dispatchers.IO)

    override fun getPlaylistPairOfSong(
        playlistId: Long,
        videoId: String,
    ): Flow<PairSongLocalPlaylist?> =
        flow {
            emit(
                localDataSource.getPlaylistPairOfSong(
                    videoId,
                    playlistId,
                ),
            )
        }.flowOn(Dispatchers.IO)

    override fun changePositionOfSongInPlaylist(
        playlistId: Long,
        videoId: String,
        newPosition: Int,
    ): Flow<String> =
        flow {
            localDataSource.editPositionOfSongInPlaylist(
                playlistId,
                videoId,
                newPosition,
            )
            delay(100)
            emit("Position updated")
        }.flowOn(Dispatchers.IO)

    /**
     * Move a song within a synced playlist.
     * 1. Resolve setVideoIds for the moved item and its new successor from the local DB
     * 2. Call YouTube API (ACTION_MOVE_VIDEO_BEFORE) to reorder on YouTube
     * 3. Update local DB positions (shift items between from/to, then place the moved item)
     */
    override fun moveItemInSyncedPlaylist(
        playlistId: Long,
        fromIndex: Int,
        toIndex: Int,
    ): Flow<LocalResource<String>> =
        moveItemInSyncedPlaylistImpl(playlistId, fromIndex, toIndex)

}