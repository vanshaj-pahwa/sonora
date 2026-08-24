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

private const val TAG = "LocalPlaylistYouTubeSync"

// Everything LocalPlaylistRepositoryImpl does with YouTube: pushing a local playlist up,
// pulling one down, and keeping set-video ids and ordering in step. Extensions on the
// repository rather than a separate collaborator, so the sync code still reaches the same
// local CRUD helpers it always did and nothing about its behaviour changes.

internal fun LocalPlaylistRepositoryImpl.syncYouTubePlaylistToLocalPlaylistImpl(
    playlist: PlaylistState,
    tracks: List<Track>,
    successMessage: String,
    errorMessage: String,
): Flow<LocalResource<String>> =
        flow<LocalResource<String>> {
            emit(LocalResource.Loading())
            val localPlaylistEntity =
                LocalPlaylistEntity(
                    title = playlist.title,
                    thumbnail = playlist.thumbnail,
                    youtubePlaylistId = playlist.id,
                    tracks = tracks.toListVideoId(),
                    downloadState = DownloadState.STATE_NOT_DOWNLOADED,
                    syncState = Syncing,
                )
            runBlocking { localDataSource.insertLocalPlaylist(localPlaylistEntity) }
            val localPlaylistId =
                localDataSource.getLocalPlaylistByYoutubePlaylistId(playlist.id)?.id
                    ?: throw Exception(errorMessage)
            tracks.forEachIndexed { i, track ->
                runBlocking {
                    localDataSource.insertSong(
                        track.toSongEntity(),
                    )
                    localDataSource.insertPairSongLocalPlaylist(
                        PairSongLocalPlaylist(
                            playlistId = localPlaylistId,
                            songId = track.videoId,
                            position = i,
                            inPlaylist = now(),
                        ),
                    )
                }
            }
            val ytPlaylistId = playlist.id
            val id = ytPlaylistId.verifyYouTubePlaylistId()
            youTube
                .customQuery(browseId = id, setLogin = true)
                .onSuccess { res ->
                    val listContent: ArrayList<MusicShelfRenderer.Content> = arrayListOf()
                    val data =
                        res.contents
                            ?.twoColumnBrowseResultsRenderer
                            ?.secondaryContents
                            ?.sectionListRenderer
                            ?.contents
                            ?.firstOrNull()
                            ?.musicPlaylistShelfRenderer
                            ?.contents
                    data?.let { listContent.addAll(it) }
                    var continueParam =
                        res.contents
                            ?.twoColumnBrowseResultsRenderer
                            ?.secondaryContents
                            ?.sectionListRenderer
                            ?.continuations
                            ?.firstOrNull()
                            ?.nextContinuationData
                            ?.continuation
                    while (continueParam != null) {
                        youTube
                            .customQuery(
                                "",
                                continuation = continueParam,
                                setLogin = true,
                            ).onSuccess { values ->
                                val dataMore: List<MusicShelfRenderer.Content>? =
                                    values.continuationContents
                                        ?.sectionListContinuation
                                        ?.contents
                                        ?.firstOrNull()
                                        ?.musicShelfRenderer
                                        ?.contents
                                if (dataMore != null) {
                                    listContent.addAll(dataMore)
                                }
                                continueParam =
                                    values.continuationContents
                                        ?.sectionListContinuation
                                        ?.continuations
                                        ?.firstOrNull()
                                        ?.nextContinuationData
                                        ?.continuation
                            }.onFailure { continueParam = null }
                    }
                    if (listContent.isEmpty()) {
                        emit(LocalResource.Error("Can't get setVideoId"))
                    }
                    val parsed = parseSetVideoId(ytPlaylistId, listContent)
                    if (parsed.isEmpty()) {
                        emit(LocalResource.Error("Can't get setVideoId"))
                    }
                    parsed.forEach { setVideoId ->
                        localDataSource.insertSetVideoId(setVideoId)
                    }
                    localDataSource.updateLocalPlaylistYouTubePlaylistSyncState(
                        localPlaylistId,
                        Synced,
                    )
                    emit(LocalResource.Success(successMessage))
                }.onFailure {
                    emit(LocalResource.Error("Can't get setVideoId"))
                }
        }.flowOn(
            Dispatchers.IO,
        )

    /**
     * Sync local playlist to YouTube playlist
     * return youtubePlaylistId
     * @param playlistId
     * @return Flow<LocalResource<String>>
     */

internal fun LocalPlaylistRepositoryImpl.syncLocalPlaylistToYouTubePlaylistImpl(
    playlistId: Long,
    successMessage: String,
    errorMessage: String,
) = flow<LocalResource<String>> {
        emit(LocalResource.Loading())
        val playlist = localDataSource.getLocalPlaylist(playlistId) ?: return@flow
        val res =
            youTube.createPlaylist(
                playlist.title,
                playlist.tracks,
            )
        val value = res.getOrNull()
        if (res.isSuccess && value != null) {
            val ytId = value.playlistId
            Logger.d(TAG, "syncLocalPlaylistToYouTubePlaylist: $ytId")
            youTube
                .getYouTubePlaylistFullTracksWithSetVideoId(ytId)
                .onSuccess { list ->
                    Logger.d(TAG, "syncLocalPlaylistToYouTubePlaylist: onSuccess song ${list.map { it.first.title }}")
                    Logger.d(TAG, "syncLocalPlaylistToYouTubePlaylist: onSuccess setVideoId ${list.map { it.second }}")
                    list.forEach { new ->
                        localDataSource.insertSong(new.first.toTrack().toSongEntity())
                        localDataSource.insertSetVideoId(
                            SetVideoIdEntity(
                                videoId = new.first.id,
                                setVideoId = new.second,
                                youtubePlaylistId = ytId,
                            ),
                        )
                    }
                    if (list.isEmpty()) Logger.w(TAG, "syncLocalPlaylistToYouTubePlaylist: SetVideoIds Empty list")
                    localDataSource.updateLocalPlaylistYouTubePlaylistId(playlistId, ytId)
                    localDataSource.updateLocalPlaylistYouTubePlaylistSyncState(playlistId, Synced)
                    Logger.d(TAG, "syncLocalPlaylistToYouTubePlaylist: $ytId")
                    emit(LocalResource.Success(ytId))
                }.onFailure {
                    emit(LocalResource.Error(it.message ?: errorMessage))
                }
        } else {
            val e = res.exceptionOrNull()
            e?.printStackTrace()
            emit(LocalResource.Error(e?.message ?: errorMessage))
        }
    }

internal fun LocalPlaylistRepositoryImpl.unsyncLocalPlaylistImpl(
    id: Long,
    successMessage: String,
) = wrapMessageResource(
        successMessage = successMessage,
    ) {
        localDataSource.unsyncLocalPlaylist(id)
    }

internal fun LocalPlaylistRepositoryImpl.updateSyncStateImpl(
    id: Long,
    syncState: Int,
    successMessage: String,
) = wrapMessageResource(
        successMessage = successMessage,
    ) {
        localDataSource.updateLocalPlaylistYouTubePlaylistSyncState(id, syncState)
    }

internal fun LocalPlaylistRepositoryImpl.updateYouTubePlaylistIdImpl(
    id: Long,
    youtubePlaylistId: String,
    successMessage: String,
) = wrapMessageResource(
        successMessage = successMessage,
    ) {
        localDataSource.updateLocalPlaylistYouTubePlaylistId(id, youtubePlaylistId)
    }

internal fun LocalPlaylistRepositoryImpl.updateListTrackSyncedImpl(
    id: Long
) =
        flow<Boolean> {
            val localPlaylist = localDataSource.getLocalPlaylist(id) ?: return@flow
            val tracks = localPlaylist.tracks ?: emptyList()
            val currentTracks = tracks.toMutableList()
            localPlaylist.youtubePlaylistId?.let { ytId ->
                Logger.d(TAG, "updateListTrackSynced: $ytId")
                youTube
                    .getYouTubePlaylistFullTracksWithSetVideoId(ytId)
                    .onSuccess { list ->
                        Logger.d(TAG, "updateListTrackSynced: onSuccess ${list.map { it.first.title }}")
                        val newTrack =
                            list
                                .map { it.first }
                                .toListTrack()
                                .map { it.videoId }
                                .toMutableSet()
                                .subtract(tracks.toMutableSet())
                        val newTrackList = list.filter { newTrack.contains(it.first.id) }
                        Logger.w(TAG, "updateListTrackSynced: newTrackList ${newTrackList.map { it.first.title }}")
                        newTrackList.forEach { new ->
                            localDataSource.insertSong(new.first.toTrack().toSongEntity())
                            Logger.i(TAG, "insertSong: ${new.first.toTrack().toSongEntity()}")
                            localDataSource.insertPairSongLocalPlaylist(
                                PairSongLocalPlaylist(
                                    playlistId = id,
                                    songId = new.first.id,
                                    position = currentTracks.size,
                                    inPlaylist = now(),
                                ),
                            )
                            localDataSource.insertSetVideoId(
                                SetVideoIdEntity(
                                    videoId = new.first.id,
                                    setVideoId = new.second,
                                    youtubePlaylistId = ytId,
                                ),
                            )
                            currentTracks.add(new.first.id)
                        }
                        localDataSource.updateLocalPlaylistTracks(currentTracks, id).let {
                            emit(true)
                        }
                    }.onFailure { e ->
                        Logger.e(TAG, "updateListTrackSynced: onFailure ${e.message}")
                        e.printStackTrace()
                        emit(false)
                    }
            }
            emit(false)
        }

    // Update

internal fun LocalPlaylistRepositoryImpl.getYouTubeSetVideoIdImpl(
    youtubePlaylistId: String
): Flow<List<SetVideoIdEntity>> =
        flow {
            runCatching {
                var id = ""
                if (!youtubePlaylistId.startsWith("VL")) {
                    id += "VL$youtubePlaylistId"
                } else {
                    id += youtubePlaylistId
                }
                Logger.d("Repository", "playlist id: $id")
                youTube
                    .customQuery(browseId = id, setLogin = true)
                    .onSuccess { result ->
                        val listContent: ArrayList<SongItem> = arrayListOf()
                        val data: List<MusicShelfRenderer.Content>? =
                            result.contents
                                ?.singleColumnBrowseResultsRenderer
                                ?.tabs
                                ?.get(
                                    0,
                                )?.tabRenderer
                                ?.content
                                ?.sectionListRenderer
                                ?.contents
                                ?.get(
                                    0,
                                )?.musicPlaylistShelfRenderer
                                ?.contents
                        var continueParam =
                            result.getPlaylistContinuation()
                        var count = 0
                        Logger.d("Repository", "playlist data: ${listContent.size}")
                        Logger.d("Repository", "continueParam: $continueParam")
                        while (continueParam != null) {
                            youTube
                                .customQuery(
                                    browseId = "",
                                    continuation = continueParam,
                                    setLogin = true,
                                ).onSuccess { values ->
                                    Logger.d("getPlaylistData", "continue: $continueParam")
                                    Logger.d(
                                        "getPlaylistData",
                                        "values: ${values.onResponseReceivedActions}",
                                    )
                                    val dataMore: List<SongItem> =
                                        values.onResponseReceivedActions
                                            ?.firstOrNull()
                                            ?.appendContinuationItemsAction
                                            ?.continuationItems
                                            ?.apply {
                                                Logger.w("getPlaylistData", "dataMore: ${this.size}")
                                            }?.mapNotNull {
                                                NextPage.fromMusicResponsiveListItemRenderer(
                                                    it.musicResponsiveListItemRenderer ?: return@mapNotNull null,
                                                )
                                            } ?: emptyList()
                                    listContent.addAll(dataMore)
                                    continueParam =
                                        values.getPlaylistContinuation()
                                    count++
                                }.onFailure {
                                    Logger.e("Continue", "Error: ${it.message}")
                                    continueParam = null
                                    count++
                                }
                        }
                        Logger.d("Repository", "playlist final data: ${listContent.size}")
                        parseSetVideoId(youtubePlaylistId, data ?: emptyList()).let { playlist ->
                            playlist.forEach { item ->
                                localDataSource.insertSetVideoId(item)
                            }
                            listContent.forEach { item ->
                                localDataSource.insertSetVideoId(
                                    SetVideoIdEntity(
                                        videoId = item.id,
                                        setVideoId = item.setVideoId,
                                        youtubePlaylistId = youtubePlaylistId,
                                    ),
                                )
                            }
                            emit(playlist)
                        }
                    }.onFailure { e ->
                        e.printStackTrace()
                        emit(emptyList())
                    }
            }
        }.flowOn(Dispatchers.IO)

internal fun LocalPlaylistRepositoryImpl.addYouTubePlaylistItemImpl(
    youtubePlaylistId: String,
    videoId: String,
): Flow<LocalResource<String>> =
        flow {
            emit(LocalResource.Loading())
            youTube
                .addPlaylistItem(youtubePlaylistId.verifyYouTubePlaylistId(), videoId)
                .onSuccess {
                    if (it.playlistEditResults.isNotEmpty()) {
                        for (playlistEditResult in it.playlistEditResults) {
                            localDataSource.insertSetVideoId(
                                SetVideoIdEntity(
                                    playlistEditResult.playlistEditVideoAddedResultData.videoId,
                                    playlistEditResult.playlistEditVideoAddedResultData.setVideoId,
                                ),
                            )
                        }
                        emit(LocalResource.Success(it.status))
                    } else {
                        emit(LocalResource.Error("FAILED"))
                    }
                }.onFailure {
                    emit(LocalResource.Error("FAILED"))
                }
        }.flowOn(Dispatchers.IO)

internal fun LocalPlaylistRepositoryImpl.moveItemInSyncedPlaylistImpl(
    playlistId: Long,
    fromIndex: Int,
    toIndex: Int,
): Flow<LocalResource<String>> =
        flow<LocalResource<String>> {
            if (fromIndex == toIndex) {
                emit(LocalResource.Success("No change"))
                return@flow
            }
            emit(LocalResource.Loading())

            val localPlaylist = localDataSource.getLocalPlaylist(playlistId) ?: run {
                emit(LocalResource.Error("Playlist not found"))
                return@flow
            }
            val ytPlaylistId = localPlaylist.youtubePlaylistId ?: run {
                emit(LocalResource.Error("Playlist is not synced with YouTube"))
                return@flow
            }

            // Get all pairs ordered by position to resolve indexes
            val allPairs = localDataSource.getAllPlaylistPairSongByPosition(playlistId)
            if (fromIndex >= allPairs.size || toIndex >= allPairs.size) {
                emit(LocalResource.Error("Index out of bounds"))
                return@flow
            }

            val movedPair = allPairs[fromIndex]
            val movedVideoId = movedPair.songId

            // Resolve setVideoId of the moved item
            val movedSetVideoIdEntity = localDataSource.getSetVideoId(movedVideoId)
            val movedSetVideoId = movedSetVideoIdEntity?.setVideoId ?: run {
                emit(LocalResource.Error("SetVideoId not found for moved item: $movedVideoId"))
                return@flow
            }

            // Resolve the successor's setVideoId (the item that should come AFTER the moved item)
            // If moving down (fromIndex < toIndex): successor is the item at toIndex + 1 (if exists)
            // If moving up (fromIndex > toIndex): successor is the item at toIndex
            val successorSetVideoId: String? = if (fromIndex < toIndex) {
                // Moving down: after removal, the successor is at toIndex + 1 in original list
                val successorIndex = toIndex + 1
                if (successorIndex < allPairs.size) {
                    val successorVideoId = allPairs[successorIndex].songId
                    localDataSource.getSetVideoId(successorVideoId)?.setVideoId
                } else {
                    null // Move to end
                }
            } else {
                // Moving up: successor is the item currently at toIndex
                val successorVideoId = allPairs[toIndex].songId
                localDataSource.getSetVideoId(successorVideoId)?.setVideoId
            }

            // Step 1: Call YouTube API
            youTube
                .movePlaylistItem(
                    playlistId = ytPlaylistId,
                    setVideoId = movedSetVideoId,
                    movedSetVideoIdSuccessor = successorSetVideoId,
                ).onSuccess {
                    Logger.d(TAG, "moveItemInSyncedPlaylist: YouTube API success, status=$it")
                }.onFailure { e ->
                    Logger.e(TAG, "moveItemInSyncedPlaylist: YouTube API failed: ${e.message}")
                    emit(LocalResource.Error("Failed to reorder on YouTube: ${e.message}"))
                    return@flow
                }

            // Step 2: Update local DB positions
            val movedPosition = movedPair.position
            val targetPosition = allPairs[toIndex].position

            if (fromIndex < toIndex) {
                // Moving down: shift items between (from, to] backward by 1
                localDataSource.shiftPositionsBackward(playlistId, movedPosition, targetPosition)
            } else {
                // Moving up: shift items between [to, from) forward by 1
                localDataSource.shiftPositionsForward(playlistId, targetPosition, movedPosition)
            }
            // Place the moved item at the target position
            localDataSource.editPositionOfSongInPlaylist(playlistId, movedVideoId, targetPosition)

            emit(LocalResource.Success("Position updated"))
        }.flowOn(Dispatchers.IO)
